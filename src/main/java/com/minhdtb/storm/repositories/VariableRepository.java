package com.minhdtb.storm.repositories;

import com.minhdtb.storm.entities.Channel;
import com.minhdtb.storm.entities.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VariableRepository extends CrudRepository<Variable, Long>, JpaRepository<Variable, Long> {

    @Transactional(readOnly = true)
    Variable findByChannelAndName(Channel channel, String name);

    @Modifying(clearAutomatically = true)
    void delete(Variable variable);
}
