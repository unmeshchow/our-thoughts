package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;

/**
 * Created by uc on 10/24/2019
 */
public interface CommentService {

    void saveCommentOfUserForPost(String userComment, User user, Post post);
}
