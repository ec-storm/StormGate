package com.minhdtb.storm.services;


import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProfileManager {

    private Profile currentProfile;

    @Autowired
    DataService dataService;

    public void setProfile(Profile profile) {
        currentProfile = profile;
    }

    public Profile getProfile() {
        return currentProfile;
    }

    public void addChannel(Channel channel) {
        channel.setProfile(currentProfile);
        currentProfile.getChannels().add(channel);
    }

    public void deleteChannel(Channel channel) {
        Channel currentChannel = currentProfile.getChannels().stream().filter(item ->
                Objects.equals(item.getName(), channel.getName())).findFirst().get();
        currentProfile.getChannels().remove(currentChannel);
    }

    public void save() {
        dataService.save(currentProfile);
    }
}
