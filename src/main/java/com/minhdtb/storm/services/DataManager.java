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
import java.util.Optional;

@Service
public class DataManager {

    @FunctionalInterface
    public interface ConsumerProfile {
        void accept(Profile profile);
    }

    @FunctionalInterface
    public interface ConsumerChannel {
        void accept(Profile profile, Channel channel);
    }

    @FunctionalInterface
    public interface ConsumerVariable {
        void accept(Variable variable);
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
        return profileRepository.findByName(profile.getName()) != null;
    }

    public void addChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().add(channel);
        channel.setProfile(currentProfile);
        channelRepository.save(channel);
        if (callback != null) {
            callback.accept(currentProfile, channel);
        }
    }

    public void deleteChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().remove(channel);
        channel.setProfile(null);
        currentProfile = profileRepository.save(currentProfile);
        channelRepository.delete(channel);
        if (callback != null) {
            callback.accept(currentProfile, channel);
        }
    }

    public boolean existChannel(Profile profile, Channel channel) {
        return channelRepository.findByProfileAndName(profile, channel.getName()) != null;
    }

    public void addVariable(Channel channel, Variable variable, ConsumerVariable callback) {
        Optional<Channel> found = currentProfile.getChannels().stream().filter(item ->
                Objects.equals(item.getName(), channel.getName())).findFirst();
        if (found.isPresent()) {
            Channel channelFound = found.get();
            if (channelFound != null) {
                channelFound.getVariables().add(variable);
                variable.setChannel(channelFound);
                variableRepository.save(variable);
                if (callback != null) {
                    callback.accept(variable);
                }
            }
        }
    }

    public void deleteVariable(Variable variable, ConsumerVariable callback) {
        Channel channel = variable.getChannel();
        Optional<Channel> found = currentProfile.getChannels().stream().filter(item ->
                Objects.equals(item.getName(), channel.getName())).findFirst();
        if (found.isPresent()) {
            Channel channelFound = found.get();
            if (channelFound != null) {
                channelFound.getVariables().remove(variable);
                variable.setChannel(null);
                variableRepository.delete(variable);
                if (callback != null) {
                    callback.accept(variable);
                }
            }
        }
    }

    public boolean existVariable(Channel channel, Variable variable) {
        return variableRepository.findByChannelAndName(channel, variable.getName()) != null;
    }
}
