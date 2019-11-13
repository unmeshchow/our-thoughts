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

    void deleteInactiveUsers(List<User> users);

    Long getUserIdByEmail(String email);

    void savePostForUserById(long userId, PostCommand postCommand);

    UserProfileDto getUserProfileById(long userId);

    void changeImageForUserById(long userId, MultipartFile imageFile);

    byte[] getImageForUserById(long userId);

    void saveCommentByUserIdAndPostId(String comment, long userId, long postId);
}
