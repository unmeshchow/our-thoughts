package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.dtos.PostCommentAdminDto;
import com.unmeshc.ourthoughts.dtos.UserAdminListDto;
import com.unmeshc.ourthoughts.dtos.UserPostAdminDto;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/26/2019
 */
public interface AdminService {

    String ADMIN_EMAIL = "admin@localhost.com";
    String ADMIN_PASSWORD = "admin";

    void createAdminUser();

    boolean isAdminExists();

    UserAdminListDto getAllUsers(int page, int size, boolean delete);

    UserPostAdminDto getPostsForUser(long userId, int page, int size, boolean delete);

    PostCommentAdminDto getCommentsForPost(long postId, int page, int size, boolean delete);

    void deleteCommentById(long commentId);

    void deletePostWithCommentsById(long postId);

    void deleteUserWithPostsById(long userId);

    void changeAdminPasswordAndLogout(String newPassword, HttpServletRequest request);

    void resetAdminPassword();
}
