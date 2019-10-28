package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by uc on 10/26/2019
 */
public interface AdminService {

    String ADMIN_EMAIL = "admin@localhost.com";
    String ADMIN_PASSWORD = "admin";

    void createAdminUser();

    boolean isAdminExists();

    Page<User> getAllUsers(Pageable pageable);

    Page<Post> getPostForUser(User user, Pageable pageable);

    User getUserById(long userId);

    Post getPostById(long postId);

    Page<Comment> getCommentForPost(Post post, Pageable pageable);

    void deleteCommentById(long commentId);

    void deletePostWithComments(long postId);

    void deleteUserWithPosts(long userId);

    void changeAdminPassword(String newPassword);

    void resetAdminPassword();
}
