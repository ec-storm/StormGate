package com.minhdtb.storm.services;

import java.util.List;

import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.repo.ProfileRepository;
import com.minhdtb.storm.repo.ChannelRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ChannelRepository channelRepository;

    public List<Profile> findAllProfile() {
        return profileRepository.findAll();
    }

    public List<Channel> findAllChannelByProfile(Profile profile) {
        return channelRepository.findByProfile(profile);
    }

    @Override
    public boolean profileExists(String profileName) {
        return profileRepository.findByName(profileName) != null;
    }

    @Transactional
    public Channel save(Channel channel) {
        return channelRepository.save(channel);
    }

    @Transactional
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }
}
