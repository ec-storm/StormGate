package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;

public interface DataService {

    Iterable<Profile> findAllProfile();

    boolean profileExists(String profileName);

    boolean channelExists(Profile profile, String channelName);

    Profile save(Profile profile);

    Channel save(Channel channel);

    void delete(Profile profile);

    void delete(Channel channel);
}
