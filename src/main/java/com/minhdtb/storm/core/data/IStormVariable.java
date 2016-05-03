package com.minhdtb.storm.core.data;


public interface IStormVariable {

    Object read();

    void write(Object value);

    IStormChannel getChannel();

    void setChannel(IStormChannel channel);

    String getName();

    String getFullName();
}
