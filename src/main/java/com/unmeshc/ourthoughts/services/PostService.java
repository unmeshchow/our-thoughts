package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by uc on 10/22/2019
 */
public interface PostService {

    void savePostForUser(User user, PostCommand postCommand);

    Post getPostById(long postId);

    Page<Post> getPostsLikeTitle(String searchValue, Pageable pageable);
}
