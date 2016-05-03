package com.minhdtb.storm.core.data;


import com.minhdtb.storm.entities.Variable;

public interface IStormVariable {

    Object read();

    void write(Object value);

    IStormChannel getChannel();

    void setChannel(IStormChannel channel);

    String getName();

    void setName(String name);

    String getFullName();

    Variable getRaw();
}
