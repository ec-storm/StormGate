package com.minhdtb.storm.repo;

import com.minhdtb.storm.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
