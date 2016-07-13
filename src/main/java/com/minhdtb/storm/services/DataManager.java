package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.repositories.ChannelRepository;
import com.minhdtb.storm.repositories.ProfileRepository;
import com.minhdtb.storm.repositories.VariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DataManager {

    @FunctionalInterface
    public interface ConsumerProfile {
        void accept(Profile profile);
    }

    @FunctionalInterface
    public interface ConsumerChannel {
        void accept(Profile profile);
    }

    @FunctionalInterface
    public interface ConsumerVariable {
        void accept(Profile profile);
    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private VariableRepository variableRepository;

    private Profile currentProfile;

    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    public void openProfile(Profile profile, ConsumerProfile callback) {
        currentProfile = profile;
        if (callback != null) {
            callback.accept(currentProfile);
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void saveProfile(Profile profile, ConsumerProfile callback) {
        Profile profileLocal = profileRepository.save(profile);
        if (callback != null) {
            callback.accept(profileLocal);
        }
    }

    public void deleteProfile(Profile profile, ConsumerProfile callback) {
        profileRepository.delete(profile);
        if (callback != null) {
            callback.accept(profile);
        }
    }

    public boolean existProfile(Profile profile) {
        Profile foundProfile = profileRepository.findByName(profile.getName());
        return foundProfile != null && !Objects.equals(foundProfile.getId(), profile.getId());
    }

    public void addChannel(Channel channel, ConsumerChannel callback) {
        channel.setProfile(currentProfile);
        currentProfile.getChannels().add(channel);
        currentProfile = profileRepository.save(currentProfile);
        if (callback != null) {
            callback.accept(currentProfile);
        }
    }

    public void saveChannel(Channel channel, ConsumerChannel callback) {
        channel = channelRepository.save(channel);
        if (callback != null) {
            callback.accept(channel.getProfile());
        }
    }

    public void deleteChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().remove(channel);
        currentProfile = profileRepository.save(currentProfile);
        if (callback != null) {
            callback.accept(currentProfile);
        }
    }

    public boolean existChannel(Profile profile, Channel channel) {
        return channelRepository.findByProfileAndName(profile, channel.getName()) != null;
    }

    public void addVariable(Channel channel, Variable variable, ConsumerVariable callback) {
        variable.setChannel(channel);
        channel.getVariables().add(variable);
        channel = channelRepository.save(channel);
        if (callback != null) {
            callback.accept(channel.getProfile());
        }
    }

    public void deleteVariable(Variable variable, ConsumerVariable callback) {
        Channel channel = variable.getChannel();
        channel.getVariables().remove(variable);
        channel = channelRepository.save(channel);
        if (callback != null) {
            callback.accept(channel.getProfile());
        }
    }

    public boolean existVariable(Channel channel, Variable variable) {
        return variableRepository.findByChannelAndName(channel, variable.getName()) != null;
    }
}
