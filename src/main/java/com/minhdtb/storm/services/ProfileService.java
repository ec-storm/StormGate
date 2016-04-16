package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Channel;

import java.util.List;

public interface ProfileService {

    Iterable<Profile> findAllProfile();

    boolean profileExists(String profileName);

    boolean channelExists(Profile profile, String channelName);

    Channel save(Channel channel);

    Profile save(Profile profile);

    void delete(Channel channel);

    void delete(Profile profile);
}
