package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by uc on 10/24/2019
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Iterable<Comment> findByPost(Post post);
}
