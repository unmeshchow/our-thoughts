package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by uc on 10/24/2019
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void saveCommentOfUserForPost(String userComment, User user, Post post) {
        Comment comment = Comment.builder().message(userComment).user(user).post(post).build();
        commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getCommentsByPost(Post post, Pageable pageable) {
        return commentRepository.findByPost(post, pageable);
    }

    @Override
    public void deleteCommentById(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentsByPost(Post post) {
        Iterable<Comment> foundPosts = commentRepository.findByPost(post);
        commentRepository.deleteAll(foundPosts);
    }

    @Override
    public List<Comment> findCommentsByPostOrderByAddingDateTime(Post post) {
        return commentRepository.findByPostOrderByAddingDateTime(post);
    }
}
