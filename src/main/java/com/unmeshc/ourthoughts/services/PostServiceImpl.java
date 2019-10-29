package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.converters.CommentToCommentPostDetailsDto;
import com.unmeshc.ourthoughts.converters.PostCommandToPost;
import com.unmeshc.ourthoughts.converters.PostToPostDetailsDto;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.CommentPostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.repositories.CommentRepository;
import com.unmeshc.ourthoughts.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uc on 10/22/2019
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostCommandToPost postCommandToPost;
    private final CommentRepository commentRepository;
    private final PostToPostDetailsDto postToPostDetailsDto;
    private final CommentToCommentPostDetailsDto commentToPostDetailsCommentDto;

    public PostServiceImpl(PostRepository postRepository,
                           PostCommandToPost postCommandToPost,
                           CommentRepository commentRepository,
                           PostToPostDetailsDto postToPostDetailsDto,
                           CommentToCommentPostDetailsDto commentToPostDetailsCommentDto) {
        this.postRepository = postRepository;
        this.postCommandToPost = postCommandToPost;
        this.commentRepository = commentRepository;
        this.postToPostDetailsDto = postToPostDetailsDto;
        this.commentToPostDetailsCommentDto = commentToPostDetailsCommentDto;
    }

    @Override
    public void savePostForUser(User user, PostCommand postCommand) {
        Post post = postCommandToPost.convert(postCommand);
        post.setPhoto(postCommand.getPostPhoto());
        post.setUser(user);

        postRepository.save(post);
    }

    @Override
    public Post getById(long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public Page<Post> getPostsLikeTitle(String searchValue, Pageable pageable) {
        return postRepository.findByTitleLikeIgnoreCase("%" + searchValue + "%", pageable);
    }

    @Override
    public PostDetailsDto getPostDetailsById(long postId) {
        Post foundPost = postRepository.findById(postId).orElse(null);
        if (foundPost == null) {
            throw new NotFoundException("Post not found with id - " + postId);
        }

        PostDetailsDto postDetailsDto = postToPostDetailsDto.convert(foundPost);
        postDetailsDto.setWriterName(foundPost.getUser().getFirstName() + " "
                + foundPost.getUser().getLastName());

        // get comments for this post
        List<CommentPostDetailsDto> postDetailsCommentDtos = new ArrayList<>();
        commentRepository.findByPostOrderByAddingDateTime(foundPost).forEach(comment -> {
            CommentPostDetailsDto postDetailsCommentDto =
                    commentToPostDetailsCommentDto.convert(comment);
            postDetailsCommentDto.setUserId(comment.getUser().getId());
            postDetailsCommentDtos.add(postDetailsCommentDto);
        });

        postDetailsDto.setPostDetailsCommentDtos(postDetailsCommentDtos);

        return postDetailsDto;
    }

    @Override
    public Page<Post> getPostsByUser(User user, Pageable pageable) {
        return postRepository.findByUser(user, pageable);
    }

    @Override
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }
}
