package com.minhdtb.storm.repo;

import java.util.List;

import com.minhdtb.storm.entities.Profile;
import com.minhdtb.storm.entities.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    List<Channel> findByProfile(Profile profile);
}
