package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Created by uc on 10/15/2019
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.email <> :email and u.active <> false")
    Page<User> findAllUserExceptAdminAndInactive(@Param("email") String email, Pageable pageable);
}
