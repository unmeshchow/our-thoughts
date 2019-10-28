package com.unmeshc.ourthoughts.repositories;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by uc on 10/24/2019
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByAddingDateTime(Post post);

    Page<Comment> findByPost(Post post, Pageable pageable);

    Iterable<Comment> findByPost(Post post);
}
