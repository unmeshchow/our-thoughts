package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by uc on 10/15/2019
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
