package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.CommentCommand;
import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.converters.CommentToCommentCommand;
import com.unmeshc.ourthoughts.converters.PostCommandToPost;
import com.unmeshc.ourthoughts.converters.PostToPostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
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
    private final PostToPostCommand postToPostCommand;
    private final CommentRepository commentRepository;
    private final CommentToCommentCommand commentToCommentCommand;

    public PostServiceImpl(PostRepository postRepository,
                           PostCommandToPost postCommandToPost,
                           ImageService imageService,
                           PostToPostCommand postToPostCommand,
                           CommentRepository commentRepository,
                           CommentToCommentCommand commentToCommentCommand) {
        this.postRepository = postRepository;
        this.postCommandToPost = postCommandToPost;
        this.imageService = imageService;
        this.postToPostCommand = postToPostCommand;
        this.commentRepository = commentRepository;
        this.commentToCommentCommand = commentToCommentCommand;
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
    public PostCommand getPostDetailsById(long postId) {
        Post foundPost = postRepository.findById(postId).orElse(null);
        if (foundPost == null) {
            throw new NotFoundException("Post not found with id - " + postId);
        }

        PostCommand postCommand = postToPostCommand.convert(foundPost);
        postCommand.setWriterName(foundPost.getUser().getFirstName() + " "
                + foundPost.getUser().getLastName());

        // get comments for this post
        List<CommentCommand> commentCommands = new ArrayList<>();
        commentRepository.findByPostOrderByAddingDateTime(foundPost).forEach(comment -> {
            CommentCommand commentCommand = commentToCommentCommand.convert(comment);
            commentCommand.setUserId(comment.getUser().getId());
            commentCommand.setPostId(comment.getPost().getId());
            commentCommands.add(commentCommand);
        });
        postCommand.setCommentCommands(commentCommands);

        return postCommand;
    }
}
