package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by uc on 10/24/2019
 */
public interface CommentService {

    void saveCommentOfUserForPost(String userComment, User user, Post post);

    Page<Comment> getCommentsByPost(Post post, Pageable pageable);

    void deleteById(long commentId);

    void deleteByPost(Post post);
}
