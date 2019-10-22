package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.User;

/**
 * Created by uc on 10/22/2019
 */
public interface PostService {

    void savePostForUser(User user, PostCommand postCommand);
}
