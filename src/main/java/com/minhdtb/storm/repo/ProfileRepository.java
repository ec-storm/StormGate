package com.minhdtb.storm.repo;

import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


public interface ProfileRepository extends CrudRepository<Profile, Long> {

    @Transactional(readOnly = true)
    Profile findByName(String name);

    @Override
    @Modifying
    void delete(Profile profile);
}
