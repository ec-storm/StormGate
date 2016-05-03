package com.minhdtb.storm.core.data;


import com.minhdtb.storm.core.engine.StormEngine;
import com.minhdtb.storm.entities.Variable;

public interface IStormVariable {

    Object getValue();

    void setValue(Object value);

    void writeValue(Object value);

    IStormChannel getChannel();

    void setChannel(IStormChannel channel);

    String getName();

    void setName(String name);

    String getFullName();

    Variable getRaw();

    StormEngine getEngine();

    void setEngine(StormEngine engine);
}
