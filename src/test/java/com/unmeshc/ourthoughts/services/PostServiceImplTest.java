package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.converters.CommentToCommentPostDetailsDto;
import com.unmeshc.ourthoughts.converters.PostCommandToPost;
import com.unmeshc.ourthoughts.converters.PostToPostDetailsDto;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.CommentRepository;
import com.unmeshc.ourthoughts.repositories.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
}