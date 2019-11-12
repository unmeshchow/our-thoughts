package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by uc on 10/24/2019
 */
public interface CommentService {

    void saveCommentOfUserForPost(String comment, User user, Post post);

    Page<Comment> getCommentsByPost(Post post, Pageable pageable);

    void deleteCommentById(long commentId);

    void deleteCommentsByPost(Post post);

    List<Comment> findCommentsByPostOrderByAddingDateTime(Post post);
}
