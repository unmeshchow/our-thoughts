package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by uc on 10/16/2019
 */
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
}
