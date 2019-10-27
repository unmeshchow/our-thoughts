package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by uc on 10/22/2019
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleLikeIgnoreCase(String title, Pageable pageable);

    Page<Post> findByUser(User user, Pageable pageable);
}
