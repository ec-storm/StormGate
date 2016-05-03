package com.minhdtb.storm.core.engine;

import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.data.*;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.services.DataManager;
import org.python.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Service
public class StormEngine {

    @Autowired
    private DataManager dataManager;

    private ScriptEngine jython;

    private List<IStormChannel> channelList = new ArrayList<>();

    private static HashMap<String, IStormVariable> variableList = new HashMap<>();

    @PostConstruct
    private void initialize() {
        PySystemState systemState = new PySystemState();
        systemState.path.add("./scripts");
        Py.setSystemState(systemState);

        ScriptEngineManager engineManager = new ScriptEngineManager();
        jython = engineManager.getEngineByName("jython");
    }

    public void start(Consumer<Profile> callback) {
        Profile profile = dataManager.getCurrentProfile();
        if (profile == null) {
            return;
        }

        try {
            jython.eval(new String(profile.getScript()));
            jython.eval(new FileReader("./scripts/engine.py"));
            PyObject main = (PyObject) jython.get("main");
            main.__call__();
        } catch (ScriptException | FileNotFoundException e) {
            Utils.writeLog(e.getMessage());
        }

        startChannels(profile);

        if (callback != null) {
            callback.accept(profile);
        }
    }

    public void stop() {
        PyObject function = (PyObject) jython.get("java_stop_thread");
        if (function != null) {
            function.__call__();
        }

        stopChannels();
    }

    private PyObject objectToPyObject(Object value) {
        PyObject result = null;

        if (value instanceof Integer) {
            result = new PyInteger((int) value);
        }

        if (value instanceof Double) {
            result = new PyFloat((double) value);
        }

        if (value instanceof String) {
            result = new PyString((String) value);
        }

        return result;
    }

    public void invokeOnChange(String variable, Object oldValue, Object newValue) {
        PyObject function = (PyObject) jython.get("java_on_change_callback");
        if (function != null) {
            function.__call__(new PyString(variable), objectToPyObject(oldValue), objectToPyObject(newValue));
        }
    }

    private void startChannels(Profile profile) {
        channelList.clear();

        for (Channel channel : profile.getChannels()) {
            switch (channel.getType()) {
                case CT_IEC_CLIENT: {
                    StormChannelIECClient stormChannelIECClient = new StormChannelIECClient(channel);
                    channelList.add(stormChannelIECClient);

                    break;
                }
                case CT_IEC_SERVER: {
                    StormChannelIECServer stormChannelIECServer = new StormChannelIECServer(channel);
                    channelList.add(stormChannelIECServer);

                    break;
                }
                case CT_OPC_CLIENT: {
                    StormChannelOPCClient stormChannelOPCClient = new StormChannelOPCClient(channel);
                    channelList.add(stormChannelOPCClient);

                    break;
                }
            }
        }

        channelList.stream().forEach(stormChannel -> {
            stormChannel.getVariables().forEach(stormVariable ->
                    variableList.put(stormVariable.getFullName(), stormVariable));

            stormChannel.start();
        });
    }

    private void stopChannels() {
        channelList.stream().forEach(IStormChannel::stop);
    }

    public static void writeVariable(String name, Object value) {
        IStormVariable variable = variableList.get(name);
        if (variable != null) {
            variable.writeValue(value);
        }
    }
}
