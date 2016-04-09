package com.minhdtb.storm.repo;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChannelRepository extends CrudRepository<Channel, Long> {

    @Modifying(clearAutomatically = true)
    List<Channel> findByProfile(Profile profile);
}
