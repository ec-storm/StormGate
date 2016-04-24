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
        callback.accept(currentProfile);
    }

    public void saveProfile(Profile profile, ConsumerProfile callback) {
        Profile profileLocal = profileRepository.save(profile);
        callback.accept(profileLocal);
    }

    public void deleteProfile(Profile profile, ConsumerProfile callback) {
        profileRepository.delete(profile);
        callback.accept(profile);
    }

    public void addChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().add(channel);
        channel.setProfile(currentProfile);
        channelRepository.save(channel);

        callback.accept(currentProfile, channel);
    }

    public void deleteChannel(Channel channel, ConsumerChannel callback) {
        currentProfile.getChannels().remove(channel);
        channel.setProfile(null);
        currentProfile = profileRepository.save(currentProfile);
        channelRepository.delete(channel);

        callback.accept(currentProfile, channel);
    }

    public void addVariable(Channel channel, Variable variable, ConsumerVariable callback) {
        Channel channelFound = currentProfile.getChannels().stream().filter(item ->
                Objects.equals(item.getName(), channel.getName())).findFirst().get();
        if (channelFound != null) {
            channelFound.getVariables().add(variable);
            variable.setChannel(channelFound);
            variableRepository.save(variable);

            callback.accept(variable);
        }
    }

    public void deleteVariable(Variable variable, ConsumerVariable callback) {
        Channel channel = variable.getChannel();
        Channel channelFound = currentProfile.getChannels().stream().filter(item ->
                Objects.equals(item.getName(), channel.getName())).findFirst().get();
        if (channelFound != null) {
            channelFound.getVariables().remove(variable);
            variable.setChannel(null);
            variableRepository.delete(variable);

            callback.accept(variable);
        }
    }
}
