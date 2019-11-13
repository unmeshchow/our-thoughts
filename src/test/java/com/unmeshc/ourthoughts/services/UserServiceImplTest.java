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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.unmeshc.ourthoughts.TestLiterals.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SearchPostPageTracker searchPostPageTracker;

    @Mock
    private CommentService commentService;

    @Mock
    private ServiceUtils serviceUtils;

    private UserMapper userMapper = UserMapper.INSTANCE;

    private PostMapper postMapper = PostMapper.INSTANCE;

    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userRepository, postRepository, commentService,
                searchPostPageTracker, userMapper, postMapper, serviceUtils);
    }

    @Test
    public void isEmailExistsTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThat(userService.isEmailExists(anyString())).isTrue();
    }

    @Test
    public void isEmailExistsFalse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThat(userService.isEmailExists(anyString())).isFalse();
    }

    @Test
    public void saveOrUpdateUser() {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveOrUpdateUser(user);

        assertThat(savedUser).isEqualTo(user);
    }

    @Test(expected = NotFoundException.class)
    public void getUserByEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenThrow(NotFoundException.class);

        userService.getUserByEmail(EMAIL);
    }

    @Test
    public void getUserByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(
                Optional.of(User.builder().id(ID).email(EMAIL).build()));

        User user = userService.getUserByEmail(EMAIL);

        assertThat(user).isNotNull();
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test(expected = NotFoundException.class)
    public void getUserByIdNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThat(userService.getUserById(ID)).isNull();
    }

    @Test
    public void getUserById() {
        User user = User.builder().id(ID).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(ID);

        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findById(ID);
    }

    @Test
    public void delete() {
        User user = User.builder().id(ID).build();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    public void getAllUsersExceptAdminAndInactive() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userRepository.findAllUserExceptAdminAndInactive(anyString(), any(Pageable.class)))
                .thenReturn(userPage);

        Page<User> foundUserPost = userService.getAllUsersExceptAdminAndInactive(
                AdminService.ADMIN_EMAIL, pageable);

        assertThat(foundUserPost).isEqualTo(userPage);
        verify(userRepository).findAllUserExceptAdminAndInactive(
                AdminService.ADMIN_EMAIL, pageable);
    }

    @Test
    public void savePostForUserById() {
        Byte[] bytes = new Byte[BYTE_SIZE];
        User user = User.builder().id(ID).build();
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        PostCommand postCommand = PostCommand.builder().multipartFile(multipartFile).build();
        when(serviceUtils.convertIntoByteArray(any(MultipartFile.class))).thenReturn(bytes);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.savePostForUserById(ID, postCommand);

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());

        Post postToSave = postArgumentCaptor.getValue();
        assertThat(postToSave.getPhoto()).isEqualTo(bytes);
        assertThat(postToSave.getUser()).isEqualTo(user);

        verify(searchPostPageTracker).newPost();
    }

    @Test
    public void getUserProfileById() {
        User user = User.builder().id(ID).image(new Byte[]{}).firstName(FIRST_NAME)
                .lastName(LAST_NAME).email(EMAIL).active(true).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserProfileDto userProfileDto = userService.getUserProfileById(ID);

        assertThat(userProfileDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(userProfileDto.getLastName()).isEqualTo(LAST_NAME);
        assertThat(userProfileDto.getEmail()).isEqualTo(EMAIL);
        assertThat(userProfileDto.isHasImage()).isTrue();
    }

    @Test
    public void changeImageForUserById() {
        UserService spyUserService = Mockito.spy(userService);
        User user = User.builder().id(ID).build();
        Byte[] bytes = new Byte[BYTE_SIZE];
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(serviceUtils.convertIntoByteArray(multipartFile)).thenReturn(bytes);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        spyUserService.changeImageForUserById(ID, multipartFile);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(spyUserService).saveOrUpdateUser(userArgumentCaptor.capture());

        User foundUser = userArgumentCaptor.getValue();
        assertThat(foundUser.getImage()).isEqualTo(bytes);
    }

    @Test
    public void getImageForUserById() {
        byte[] bytes = new byte[BYTE_SIZE];
        User user = User.builder().id(ID).image(new Byte[]{}).build();
        when(serviceUtils.convertIntoByteArray(user.getImage())).thenReturn(bytes);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        byte[] byteImage = userService.getImageForUserById(ID);

        assertThat(byteImage).isEqualTo(bytes);
    }

    @Test(expected = NotFoundException.class)
    public void saveCommentByUserIdAndPostIdNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        userService.saveCommentByUserIdAndPostId(COMMENT, ID, ID);
    }

    @Test
    public void saveCommentByUserIdAndPostId() {
        User user = User.builder().id(ID).build();
        Post post = Post.builder().id(ID).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.saveCommentByUserIdAndPostId(COMMENT, ID, ID);

        verify(commentService).saveCommentOfUserForPost(COMMENT, user, post);
    }

    @Test
    public void deleteInactiveUsers() {
        List<User> users = Arrays.asList(User.builder().id(ID).build());

        userService.deleteInactiveUsers(users);

        verify(userRepository).deleteAll(users);
    }
}