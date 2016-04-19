package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.Modifying;

public interface ProfileService {

    Iterable<Profile> findAllProfile();

    boolean profileExists(String profileName);

    boolean channelExists(Profile profile, String channelName);

    Profile save(Profile profile);

    @Modifying(clearAutomatically = true)
    void delete(Profile profile);
}
