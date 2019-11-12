package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by uc on 10/15/2019
 */
public interface UserService {

    boolean isEmailExists(String email);

    User saveOrUpdateUser(User user);

    User getUserByEmail(String email);

    User getUserById(long userId);

    void deleteUser(User user);

    Page<User> getAllUsersExceptAdminAndInactive(String adminEmail, Pageable pageable);

    void savePostForUser(User user, PostCommand postCommand);

    UserProfileDto getUserProfile(User user);

    void changeImageForUser(User user, MultipartFile imageFile);

    byte[] getImageForUser(User user);

    void saveCommentOfUserForPost(String comment, User user, long postId);

    void deleteInactiveUsers(List<User> users);
}
