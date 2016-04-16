package com.minhdtb.storm.repo;

import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


public interface ProfileRepository extends CrudRepository<Profile, Long>, JpaRepository<Profile, Long> {

    @Transactional(readOnly = true)
    Profile findByName(String name);

    @Modifying(clearAutomatically = true)
    void delete(Profile profile);
}
