package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Channel;

import java.util.List;

public interface IStormChannel {

    void start();

    void stop();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    List<IStormVariable> getVariables();

    Channel getRaw();
}
