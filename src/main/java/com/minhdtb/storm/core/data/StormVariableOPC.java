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

    public int getDataType() {
        return Integer.parseInt(getAttribute("dataType"));
    }

    public void setDataType(int dataType) {
        setAttribute("dataType", String.valueOf(dataType));
    }

    @Override
    public void write(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelOPC) {
            ((StormChannelOPC) channel).write(getTagName(), value);
        }
    }
}
