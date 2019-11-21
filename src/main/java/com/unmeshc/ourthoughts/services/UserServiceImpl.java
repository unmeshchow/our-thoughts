package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserProfileDto;
import com.unmeshc.ourthoughts.mappers.PostMapper;
import com.unmeshc.ourthoughts.mappers.UserMapper;
import com.unmeshc.ourthoughts.repositories.PostRepository;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.pagination.SearchPostPageTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by uc on 10/15/2019
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final SearchPostPageTracker searchPostPageTracker;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final ServiceUtils serviceUtils;

    public UserServiceImpl(UserRepository userRepository,
                           PostRepository postRepository,
                           CommentService commentService,
                           SearchPostPageTracker searchPostPageTracker,
                           UserMapper userMapper,
                           PostMapper postMapper,
                           ServiceUtils serviceUtils) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentService = commentService;
        this.searchPostPageTracker = searchPostPageTracker;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException("User not found with email - " + email));
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id - " + userId));
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Page<User> getAllUsersExceptAdminAndInactive(String adminEmail,
                                                        Pageable pageable) {

        return userRepository.findAllUserExceptAdminAndInactive(adminEmail, pageable);
    }

    @Override
    public void deleteInactiveUsers(List<User> users) {
        userRepository.deleteAll(users);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return getUserByEmail(email).getId();
    }

    @Override
    public void savePostForUserById(long userId, PostCommand postCommand) {
        try {
            Post post = postMapper.postCommandToPost(postCommand);
            post.setPhoto(serviceUtils.convertIntoByteArray(postCommand.getMultipartFile()));
            post.setUser(getUserById(userId));
            postRepository.save(post);
            searchPostPageTracker.newPost();
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    @Override
    public UserProfileDto getUserProfileById(long userId) {
        User user = getUserById(userId);
        UserProfileDto userProfileDto = userMapper.userToUserProfileDto(user);
        userProfileDto.setHasImage(user.hasImage());
        return userProfileDto;
    }

    @Override
    public void changeImageForUserById(long userId, MultipartFile imageFile) {
        User user = getUserById(userId);
        user.setImage(serviceUtils.convertIntoByteArray(imageFile));
        saveOrUpdateUser(user);
    }

    @Override
    public byte[] getImageForUserById(long userId) {
        return  getUserById(userId).getImage();
    }

    @Override
    public void saveCommentByUserIdAndPostId(String comment, long userId, long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("Post not found with id - "+ postId));

        commentService.saveCommentOfUserForPost(comment, getUserById(userId), post);
    }
}
