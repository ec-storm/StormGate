package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataManager {

    @FunctionalInterface
    public interface ConsumerChannel {
        void accept(Profile profile, Channel channel);
    }

    @FunctionalInterface
    public interface ConsumerProfile {
        void accept(Profile profile);
    }

    @Autowired
    private DataService service;

    private Profile currentProfile;

    public void openProfile(Profile profile, ConsumerProfile callback) {
        currentProfile = profile;
        callback.accept(currentProfile);
    }

    public void saveProfile(Profile profile, ConsumerProfile callback) {
        Profile profileLocal = service.save(profile);
        callback.accept(profileLocal);
    }

    public void deleteProfile(Profile profile, ConsumerProfile callback) {
        service.delete(profile);
        callback.accept(profile);
    }

    public void addChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().add(channel);
        channel.setProfile(currentProfile);
        service.save(channel);

        callback.accept(currentProfile, channel);
    }

    public void deleteChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().remove(channel);
        channel.setProfile(null);
        currentProfile = service.save(currentProfile);
        service.delete(channel);

        callback.accept(currentProfile, channel);
    }
}
