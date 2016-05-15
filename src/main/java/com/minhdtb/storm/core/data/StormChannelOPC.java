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

    public String getProgId() {
        return getAttribute("progId");
    }

    public void setProgId(String progId) {
        setAttribute("progId", progId);
    }

    public int getRefreshRate() {
        return Integer.parseInt(getAttribute("refreshRate"));
    }

    public void setRefreshRate(int refreshRate) {
        setAttribute("refreshRate", String.valueOf(refreshRate));
    }

    @Override
    public List<IStormVariable> getVariables() {
        return variables;
    }
}
