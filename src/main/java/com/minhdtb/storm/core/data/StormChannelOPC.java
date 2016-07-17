package com.minhdtb.storm.core.data;


import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Variable;

import java.util.ArrayList;
import java.util.List;

abstract class StormChannelOPC extends StormChannel {

    private List<IStormVariable> variables = new ArrayList<>();

    StormChannelOPC() {
        super();
    }

    StormChannelOPC(Channel channel) {
        super(channel);

        for (Variable variable : getRaw().getVariables()) {
            StormVariableOPC variableOPC = new StormVariableOPC(variable);
            variableOPC.setChannel(this);
            variables.add(variableOPC);
        }
    }

    public static final String HOST = "host";
    public static final String PROG_ID = "progId";
    public static final String REFRESH_RATE = "refreshRate";

    public String getHost() {
        return getAttribute(HOST);
    }

    public void setHost(String host) {
        setAttribute(HOST, host);
    }

    public String getProgId() {
        return getAttribute(PROG_ID);
    }

    public void setProgId(String progId) {
        setAttribute(PROG_ID, progId);
    }

    public int getRefreshRate() {
        return Integer.parseInt(getAttribute(REFRESH_RATE));
    }

    public void setRefreshRate(int refreshRate) {
        setAttribute(REFRESH_RATE, refreshRate);
    }

    @Override
    public List<IStormVariable> getVariables() {
        return variables;
    }

    public abstract void write(String tagName, Object value);
}
