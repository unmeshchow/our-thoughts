package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by uc on 10/22/2019
 */
public interface PostService {

    void savePostForUser(User user, PostCommand postCommand);

    Post getById(long postId);

    Page<Post> getPostsLikeTitle(String searchValue, Pageable pageable);

    PostDetailsDto getPostDetailsById(long postId);

    Page<Post> getPostsByUser(User user, Pageable pageable);

    void delete(Post post);

    List<Post> getPostsByUser(User user);
}
