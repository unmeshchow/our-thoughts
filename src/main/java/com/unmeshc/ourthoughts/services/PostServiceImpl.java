package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.converters.CommentToCommentDto;
import com.unmeshc.ourthoughts.converters.PostCommandToPost;
import com.unmeshc.ourthoughts.converters.PostToPostDetailsDto;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.CommentDto;
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
    private final ImageService imageService;
    private final CommentRepository commentRepository;
    private final PostToPostDetailsDto postToPostDetailsDto;
    private final CommentToCommentDto commentToCommentDto;

    public PostServiceImpl(PostRepository postRepository,
                           PostCommandToPost postCommandToPost,
                           ImageService imageService,
                           CommentRepository commentRepository,
                           PostToPostDetailsDto postToPostDetailsDto,
                           CommentToCommentDto commentToCommentDto) {
        this.postRepository = postRepository;
        this.postCommandToPost = postCommandToPost;
        this.imageService = imageService;
        this.commentRepository = commentRepository;
        this.postToPostDetailsDto = postToPostDetailsDto;
        this.commentToCommentDto = commentToCommentDto;
    }

    @Override
    public void savePostForUser(User user, PostCommand postCommand) {
        Post post = postCommandToPost.convert(postCommand);
        post.setPhoto(imageService.convertIntoByteArray(postCommand.getPhoto()));
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
        List<CommentDto> commentDtos = new ArrayList<>();
        commentRepository.findByPostOrderByAddingDateTime(foundPost).forEach(comment -> {
            CommentDto commentDto = commentToCommentDto.convert(comment);
            commentDto.setUserId(comment.getUser().getId());
            commentDtos.add(commentDto);
        });

        postDetailsDto.setCommentDtos(commentDtos);

        return postDetailsDto;
    }
}
