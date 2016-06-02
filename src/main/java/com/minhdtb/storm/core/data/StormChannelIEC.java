package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Variable;

import java.util.ArrayList;
import java.util.List;

abstract class StormChannelIEC extends StormChannel {

    protected final int TIMEOUT = 5000;

    public static final String HOST = "host";
    public static final String PORT = "port";

    private List<IStormVariable> variables = new ArrayList<>();

    StormChannelIEC(Channel channel) {
        super(channel);

        for (Variable variable : getRaw().getVariables()) {
            StormVariableIEC variableIEC = new StormVariableIEC(variable);
            variableIEC.setChannel(this);
            variables.add(variableIEC);
        }
    }

    @Override
    public List<IStormVariable> getVariables() {
        return variables;
    }

    public abstract void send(ASdu aSdu);

    StormChannelIEC() {
        super();
    }

    String getHost() {
        return getAttribute(HOST);
    }

    public void setHost(String host) {
        setAttribute(HOST, host);
    }

    int getPort() {
        return Integer.parseInt(getAttribute(PORT));
    }

    public void setPort(int port) {
        setAttribute(PORT, String.valueOf(port));
    }
}
