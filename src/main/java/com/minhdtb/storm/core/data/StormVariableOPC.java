package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Variable;

public class StormVariableOPC extends StormVariable {

    public StormVariableOPC() {
        super();
    }

    StormVariableOPC(Variable variable) {
        super(variable);
    }

    public String getTagName() {
        return getAttribute("tagName");
    }

    public void setTagName(String tagName) {
        setAttribute("tagName", tagName);
    }

    @Override
    public void write(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelOPC) {
            ((StormChannelOPC) channel).write(getTagName(), value);
        }
    }
}
