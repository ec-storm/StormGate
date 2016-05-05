package com.minhdtb.storm.core.data;

import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Variable;

import java.util.ArrayList;
import java.util.List;

abstract class StormChannelIEC extends StormChannel {

    private List<IStormVariable> variables = new ArrayList<>();

    String getHost() {
        return getAttribute("host");
    }

    public void setHost(String host) {
        setAttribute("host", host);
    }

    int getPort() {
        return Integer.parseInt(getAttribute("port"));
    }

    public void setPort(int port) {
        setAttribute("port", String.valueOf(port));
    }

    StormChannelIEC() {
        super();
    }

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
}
