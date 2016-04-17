package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;

public interface ProfileService {

    Iterable<Profile> findAllProfile();

    boolean profileExists(String profileName);

    boolean channelExists(Profile profile, String channelName);

    Profile save(Profile profile);

    Channel save(Channel channel);

    Variable save(Variable variable);

    void delete(Profile profile);

    void delete(Channel channel);

    void delete(Variable variable);
}
