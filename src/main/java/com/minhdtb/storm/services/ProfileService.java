package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Channel;

import java.util.List;

public interface ProfileService {

    Iterable<Profile> findAllProfile();

    List<Channel> findAllChannelByProfile(Profile profile);

    boolean profileExists(String profileName);

    Channel save(Channel channel);

    Profile save(Profile profile);

    void delete(Channel channel);

    void delete(Profile profile);
}
