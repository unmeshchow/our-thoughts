package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by uc on 10/22/2019
 */
public interface PostService {

    Post getPostById(long postId);

    PostSearchListDto getPostsByTitleLike(int page, int size, String title);

    PostDetailsDto getPostDetailsById(long postId);

    Page<Post> getPostsByUser(User user, Pageable pageable);

    void deletePost(Post post);

    List<Post> getPostsByUser(User user);

    byte[] getPostPhotoById(long postId);

    byte[] getUserImageById(long userId);
}
