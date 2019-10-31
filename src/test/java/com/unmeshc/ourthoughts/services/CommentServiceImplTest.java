package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.CommentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveCommentOfUserForPost() {
        String userComment = "Comment";
        User user = User.builder().id(1L).build();
        Post post = Post.builder().id(1L).build();

        service.saveCommentOfUserForPost(userComment, user, post);

        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment comment = commentArgumentCaptor.getValue();
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getMessage()).isEqualTo("Comment");
    }

    @Test
    public void getCommentsByPost() {
        Post post = Post.builder().id(1L).build();
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Comment> commentPage = Mockito.mock(Page.class);
        when(commentRepository.findByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);

        Page<Comment> foundCommentPage = service.getCommentsByPost(post, pageable);

        assertThat(foundCommentPage).isEqualTo(commentPage);
        verify(commentRepository).findByPost(post, pageable);
    }

    @Test
    public void deleteById() {
        service.deleteById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    public void deleteByPost() {
        Post post = Post.builder().id(1L).build();
        Iterable<Comment> posts = new ArrayList<>();
        ((ArrayList<Comment>) posts).add(Comment.builder().id(1L).build());
        ((ArrayList<Comment>) posts).add(Comment.builder().id(2L).build());
        when(commentRepository.findByPost(any(Post.class))).thenReturn(posts);

        service.deleteByPost(post);

        verify(commentRepository).findByPost(post);
        verify(commentRepository).deleteAll(posts);
    }
}