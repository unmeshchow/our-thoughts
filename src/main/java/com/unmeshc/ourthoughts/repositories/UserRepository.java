package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by uc on 10/15/2019
 */
public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByEmail(String email);
}
