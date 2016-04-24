package com.minhdtb.storm.repositories;

import com.minhdtb.storm.entities.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableRepository extends CrudRepository<Variable, Long>, JpaRepository<Variable, Long> {

    @Modifying(clearAutomatically = true)
    void delete(Variable variable);
}
