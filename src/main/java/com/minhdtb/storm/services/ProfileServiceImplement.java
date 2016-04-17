package com.minhdtb.storm.services;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Variable;
import com.minhdtb.storm.repo.ChannelRepository;
import com.minhdtb.storm.repo.ProfileRepository;
import com.minhdtb.storm.repo.VariableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProfileServiceImplement implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ChannelRepository channelRepository;
    private final VariableRepository variableRepository;

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
    public Channel save(Channel channel) {
        return channelRepository.saveAndFlush(channel);
    }

    @Override
    public Variable save(Variable variable) {
        return variableRepository.saveAndFlush(variable);
    }

    @Transactional
    public Profile save(Profile profile) {
        return profileRepository.saveAndFlush(profile);
    }

    @Override
    public void delete(Channel channel) {
        channelRepository.delete(channel);
    }

    @Override
    public void delete(Variable variable) {
        variableRepository.delete(variable);
    }

    @Override
    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }
}
