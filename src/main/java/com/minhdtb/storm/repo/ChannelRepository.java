package com.minhdtb.storm.repo;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


public interface ChannelRepository extends CrudRepository<Channel, Long>, JpaRepository<Channel, Long> {

    @Transactional(readOnly = true)
    Channel findByProfileAndName(Profile profile, String name);

    @Modifying(clearAutomatically = true)
    void delete(Channel channel);
}
