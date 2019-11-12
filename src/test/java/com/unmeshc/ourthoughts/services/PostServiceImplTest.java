package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchListDto;
import com.unmeshc.ourthoughts.mappers.CommentMapper;
import com.unmeshc.ourthoughts.mappers.PostMapper;
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

import java.util.*;

import static com.unmeshc.ourthoughts.TestLiterals.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PostServiceImplTest {

    @Mock
    private CommentService commentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchPostPageTracker searchPostPageTracker;

    @Mock
    private ServiceUtils serviceUtils;

    private PostMapper postMapper = PostMapper.INSTANCE;

    private CommentMapper commentMapper = CommentMapper.INSTANCE;

    private PostService postService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        postService = new PostServiceImpl(postRepository, userRepository, commentService,
                searchPostPageTracker, postMapper, commentMapper, serviceUtils);
    }

    @Test(expected = NotFoundException.class)
    public void getPostByIdNotFound() {
        when(postRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        postService.getPostById(ID);
    }

    @Test
    public void getPostById() {
        Post post = Post.builder().id(ID).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Post returnPost = postService.getPostById(ID);
        assertThat(returnPost).isEqualTo(post);
        verify(postRepository).findById(ID);
    }

    @Test
    public void getPostsTitleLikeWithDefaultValue() { // 0, 0, ""
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        String searchValue = "";
        List<Post> posts = Arrays.asList(Post.builder().build(), Post.builder().build());
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postRepository.findByTitleLikeIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(postPage);
        when(searchPostPageTracker.getSearchValue()).thenReturn(UNMESH);
        when(searchPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postPage.stream()).thenReturn(posts.stream());
        when(searchPostPageTracker.getPageNumbersForPagination(postPage)).thenReturn(pageNumbers);

        PostSearchListDto postSearchListDto =
                postService.getPostsByTitleLike(0, 0, searchValue);

        assertThat(postSearchListDto.getCurrentPage()).isEqualTo(1);
        assertThat(postSearchListDto.getPageNumbers()).isEqualTo(pageNumbers);
        assertThat(postSearchListDto.getSearchValue()).isEqualTo(searchValue);
        assertThat(postSearchListDto.getPostSearchDtos().size()).isEqualTo(2);

        verify(searchPostPageTracker).reset();
        verify(searchPostPageTracker, times(2)).getCurrentPage();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(postRepository).findByTitleLikeIgnoreCase(stringArgumentCaptor.capture(),
                pageableArgumentCaptor.capture());

        String searchKey = stringArgumentCaptor.getValue();
        assertThat(searchKey).isEqualTo("%" + searchValue + "%");

        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(searchPostPageTracker).setSearchValue(searchValue);
        verify(searchPostPageTracker).setCurrentPage(1);
    }

    @Test
    public void getPostsTitleLikeWithValues() {
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        String searchValue = "Madonna";
        List<Post> posts = Arrays.asList(Post.builder().build(), Post.builder().build());
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postRepository.findByTitleLikeIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(postPage);
        when(searchPostPageTracker.getSearchValue()).thenReturn(searchValue);
        when(searchPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postPage.stream()).thenReturn(posts.stream());
        when(searchPostPageTracker.getPageNumbersForPagination(postPage)).thenReturn(pageNumbers);

        PostSearchListDto postSearchListDto =
                postService.getPostsByTitleLike(2, 5, searchValue);

        assertThat(postSearchListDto.getCurrentPage()).isEqualTo(1);
        assertThat(postSearchListDto.getPageNumbers()).isEqualTo(pageNumbers);
        assertThat(postSearchListDto.getSearchValue()).isEqualTo(searchValue);
        assertThat(postSearchListDto.getPostSearchDtos().size()).isEqualTo(2);

        verify(searchPostPageTracker).getCurrentPage();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(postRepository).findByTitleLikeIgnoreCase(stringArgumentCaptor.capture(),
                pageableArgumentCaptor.capture());

        String searchKey = stringArgumentCaptor.getValue();
        assertThat(searchKey).isEqualTo("%" + searchValue + "%");

        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(searchPostPageTracker).setSearchValue(searchValue);
        verify(searchPostPageTracker).setCurrentPage(1);
    }

    @Test
    public void getPostDetailsById() {
        User user = User.builder().id(ID).firstName(FIRST_NAME).lastName(LAST_NAME).build();
        Post post = Post.builder().id(ID).title(TITLE).body(BODY).caption(CAPTION)
                .user(user).build();
        List<Comment> comments = Arrays.asList(
                Comment.builder().id(ID).user(user).build(),
                Comment.builder().id(2L).user(user).build()
        );
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentService.findCommentsByPostOrderByAddingDateTime(any(Post.class)))
                .thenReturn(comments);

        PostDetailsDto postDetailsDto = postService.getPostDetailsById(ID);

        assertThat(postDetailsDto.getWriterName()).isEqualTo(FULL_NAME);
        assertThat(postDetailsDto.getTitle()).isEqualTo(TITLE);
        assertThat(postDetailsDto.getBody()).isEqualTo(BODY);
        assertThat(postDetailsDto.getCaption()).isEqualTo(CAPTION);
        assertThat(postDetailsDto.getCommentPostDetailsDtos().size()).isEqualTo(2);
    }

    @Test
    public void getPostsByUser() {
        User user = User.builder().id(ID).build();
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postRepository.findByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);

        Page<Post> foundPostPage = postService.getPostsByUser(user, pageable);

        assertThat(foundPostPage).isEqualTo(postPage);
        verify(postRepository).findByUser(user, pageable);
    }

    @Test
    public void deletePost() {
        Post post = Post.builder().id(ID).build();
        postService.deletePost(post);
        verify(postRepository).delete(post);
    }

    @Test
    public void getPostsByUser2() {
        List<Post> posts = Arrays.asList(Post.builder().id(ID).build());
        User user = User.builder().id(ID).build();
        when(postRepository.findByUser(any(User.class))).thenReturn(posts);

        List<Post> foundPosts = postService.getPostsByUser(user);

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findByUser(user);

    }

    @Test
    public void getPostPhotoById() {
        byte[] bytes = new byte[BYTE_SIZE];
        Post post = Post.builder().id(ID).photo(new Byte[]{}).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(serviceUtils.convertIntoByteArray(post.getPhoto())).thenReturn(bytes);

        byte[] foundBytes = postService.getPostPhotoById(ID);

        assertThat(foundBytes).isEqualTo(bytes);
    }

    @Test
    public void getUserImageById() {
        byte[] bytes = new byte[BYTE_SIZE];
        User user = User.builder().id(ID).image(new Byte[]{}).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(serviceUtils.convertIntoByteArray(user.getImage())).thenReturn(bytes);

        byte[] foundBytes = postService.getUserImageById(ID);

        assertThat(foundBytes).isEqualTo(bytes);
    }

    @Test(expected = NotFoundException.class)
    public void getUserImageByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        postService.getUserImageById(ID);
    }
}
