package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.Token;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by uc on 10/16/2019
 */
public interface TokenRepository extends CrudRepository<Token, Long> {
}
