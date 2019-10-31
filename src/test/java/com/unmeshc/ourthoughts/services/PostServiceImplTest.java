package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.converters.CommentToCommentPostDetailsDto;
import com.unmeshc.ourthoughts.converters.PostCommandToPost;
import com.unmeshc.ourthoughts.converters.PostToPostDetailsDto;
import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.CommentPostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.repositories.CommentRepository;
import com.unmeshc.ourthoughts.repositories.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommandToPost postCommandToPost;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostToPostDetailsDto postToPostDetailsDto;

    @Mock
    private CommentToCommentPostDetailsDto commentToCommentPostDetailsDto;

    @InjectMocks
    private PostServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void savePostForUser() {
        Byte[] bytes = new Byte[10];
        User user = User.builder().id(1L).build();
        PostCommand postCommand = PostCommand.builder().postPhoto(bytes).build();
        Post post = Post.builder().id(1L).build();
        when(postCommandToPost.convert(any(PostCommand.class))).thenReturn(post);

        service.savePostForUser(user, postCommand);

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postArgumentCaptor.capture());
        Post postToSave = postArgumentCaptor.getValue();
        assertThat(postToSave.getPhoto()).isEqualTo(bytes);
        assertThat(postToSave.getUser()).isEqualTo(user);
    }

    @Test
    public void getByIdNull() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThat(service.getById(1L)).isNull();
        verify(postRepository).findById(1L);
    }

    @Test
    public void getById() {
        Post post = Post.builder().id(1L).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Post returnPost = service.getById(1L);
        assertThat(returnPost).isEqualTo(post);
        verify(postRepository).findById(1L);
    }

    @Test
    public void getPostsTitleLike() {
        String searchValue = "beautiful";
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postRepository.findByTitleLikeIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(postPage);

        Page<Post> foundPostPage = service.getPostsTitleLike(searchValue, pageable);

        assertThat(foundPostPage).isEqualTo(postPage);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(postRepository).findByTitleLikeIgnoreCase(stringArgumentCaptor.capture(), eq(pageable));
        String searchKey = stringArgumentCaptor.getValue();
        assertThat(searchKey).isEqualTo("%" + searchValue + "%");
    }

    @Test(expected = NotFoundException.class)
    public void getPostDetailsByIdNotFoundException() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        service.getPostDetailsById(1L);
    }

    @Test
    public void getPostDetailsById() {
        User user = User.builder().id(1L).firstName("Unmesh").lastName("Chowdhury").build();
        Post post = Post.builder().id(1L).user(user).build();
        PostDetailsDto postDetailsDto = PostDetailsDto.builder().id(1L).title("title")
                .body("body").caption("caption").build();
        List<Comment> comments = Arrays.asList(
                Comment.builder().id(1L).user(user).build(),
                Comment.builder().id(2L).user(user).build()
        );
        CommentPostDetailsDto commentPostDetailsDto = CommentPostDetailsDto.builder().build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postToPostDetailsDto.convert(any(Post.class))).thenReturn(postDetailsDto);
        when(commentRepository.findByPostOrderByAddingDateTime(any(Post.class))).thenReturn(comments);
        when(commentToCommentPostDetailsDto.convert(any(Comment.class))).thenReturn(commentPostDetailsDto);

        PostDetailsDto foundPostDetailsDto = service.getPostDetailsById(1L);

        assertThat(foundPostDetailsDto.getWriterName()).isEqualTo("Unmesh Chowdhury");
        assertThat(foundPostDetailsDto.getTitle()).isEqualTo("title");
        assertThat(foundPostDetailsDto.getBody()).isEqualTo("body");
        assertThat(foundPostDetailsDto.getCaption()).isEqualTo("caption");
        assertThat(foundPostDetailsDto.getPostDetailsCommentDtos().size()).isEqualTo(2);
        assertThat(foundPostDetailsDto.getPostDetailsCommentDtos().get(0).getUserId()).isEqualTo(1L);
        assertThat(foundPostDetailsDto.getPostDetailsCommentDtos().get(1).getUserId()).isEqualTo(1L);
    }

    @Test
    public void getPostsByUser() {
        User user = User.builder().id(1L).build();
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(postPage);

        Page<Post> foundPostPage = service.getPostsByUser(user, pageable);

        assertThat(foundPostPage).isEqualTo(postPage);
        verify(postRepository).findByUser(user, pageable);
    }

    @Test
    public void delete() {
        Post post = Post.builder().id(1L).build();
        service.delete(post);
        verify(postRepository).delete(post);
    }

    @Test
    public void getPostsByUser2() {
        List<Post> posts = Arrays.asList(Post.builder().id(1L).build());
        User user = User.builder().id(1L).build();
        when(postRepository.findByUser(any(User.class))).thenReturn(posts);

        List<Post> foundPosts = service.getPostsByUser(user);

        assertThat(foundPosts).isEqualTo(posts);
        verify(postRepository).findByUser(user);

    }
}