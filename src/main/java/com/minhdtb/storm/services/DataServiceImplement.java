package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.repositories.ChannelRepository;
import com.minhdtb.storm.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DataServiceImplement implements DataService {

    private final ProfileRepository profileRepository;
    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public Iterable<Profile> findAllProfile() {
        return profileRepository.findAll();
    }

    @Override
    public boolean profileExists(String profileName) {
        return profileRepository.findByName(profileName) != null;
    }

    @Override
    public boolean channelExists(Profile profile, String channelName) {
        return channelRepository.findByProfileAndName(profile, channelName) != null;
    }

    @Transactional
    @Override
    public Profile save(Profile profile) {
        return profileRepository.saveAndFlush(profile);
    }

    @Transactional
    @Override
    public Channel save(Channel channel) {
        return channelRepository.saveAndFlush(channel);
    }

    @Transactional
    @Override
    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

    @Transactional
    @Override
    public void delete(Channel channel) {
        channelRepository.delete(channel);
    }
}
