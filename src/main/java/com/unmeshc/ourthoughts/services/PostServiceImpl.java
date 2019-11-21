package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.CommentPostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchDto;
import com.unmeshc.ourthoughts.dtos.PostSearchListDto;
import com.unmeshc.ourthoughts.mappers.CommentMapper;
import com.unmeshc.ourthoughts.mappers.PostMapper;
import com.unmeshc.ourthoughts.repositories.PostRepository;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.pagination.SearchPostPageTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by uc on 10/22/2019
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private static final int PAGE_SIZE = 12;
    private static final int TITLE_SIZE = 20;
    private static final int BODY_SIZE = 60;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final SearchPostPageTracker searchPostPageTracker;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ServiceUtils serviceUtils;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           CommentService commentService,
                           SearchPostPageTracker searchPostPageTracker,
                           PostMapper postMapper,
                           CommentMapper commentMapper,
                           ServiceUtils serviceUtils) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.searchPostPageTracker = searchPostPageTracker;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException("Post not found with id - " + postId)
        );
    }

    @Override
    public PostSearchListDto getPostsByTitleLike(int page, int size, String title) {
        if (title.equals("")) {
            title = searchPostPageTracker.getSearchValue();
        }

        if (!title.equalsIgnoreCase(searchPostPageTracker.getSearchValue())) {
            searchPostPageTracker.reset();
        }

        int currentPage = page > 0 ? page : searchPostPageTracker.getCurrentPage();
        int pageSize = size > 0 ? size : PAGE_SIZE;

        // zero based page number
        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending());

        // get post page
        Page<Post> postPage = postRepository.findByTitleLikeIgnoreCase(
                "%" + title + "%", pageable);

        // set the current page and search value in the page tracker - current page is 1 based
        searchPostPageTracker.setCurrentPage(postPage.getNumber() + 1);
        searchPostPageTracker.setSearchValue(title);

        // convert post to post search dto
        List<PostSearchDto> postSearchDtos = postPage
                .stream()
                .map(post -> PostSearchDto.builder()
                           .id(post.getId())
                           .title(serviceUtils.addSpacesOrEllipsis(post.getTitle(), TITLE_SIZE))
                           .body(serviceUtils.addSpacesOrEllipsis(post.getBody(), BODY_SIZE))
                           .build())
                .collect(Collectors.toList());

        // create and return post search list dto
        return PostSearchListDto.builder()
                .currentPage(searchPostPageTracker.getCurrentPage())
                .pageNumbers(searchPostPageTracker.getPageNumbersForPagination(postPage))
                .searchValue(title)
                .postSearchDtos(postSearchDtos)
                .build();
    }

    @Override
    public PostDetailsDto getPostDetailsById(long postId) {
        Post post = getPostById(postId);

        // get comments for this post and set user id and has image in the comments
        List<CommentPostDetailsDto> commentPostDetailsDtos =
                commentService.findCommentsByPostOrderByAddingDateTime(post)
                .stream()
                .map(comment -> {
                    CommentPostDetailsDto commentPostDetailsDto =
                            commentMapper.commentToCommentPostDetailsDto(comment);
                    commentPostDetailsDto.setUserId(comment.getUser().getId());
                    commentPostDetailsDto.setUserHasImage(comment.getUser().hasImage());
                    return commentPostDetailsDto;
                })
                .collect(Collectors.toList());

        // create post details dto
        PostDetailsDto postDetailsDto = postMapper.postToPostDetailsDto(post);
        postDetailsDto.setWriterName(post.getUser().getFirstName() + " " +
                post.getUser().getLastName());
        postDetailsDto.setCommentPostDetailsDtos(commentPostDetailsDtos);

        return postDetailsDto;
    }

    @Override
    public Page<Post> getPostsByUser(User user, Pageable pageable) {
        return postRepository.findByUser(user, pageable);
    }

    @Override
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    @Override
    public byte[] getPostPhotoById(long postId) {
        return getPostById(postId).getPhoto();
    }

    @Override
    public byte[] getUserImageById(long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getImage())
                .orElseThrow(() -> new NotFoundException("User not found with id - " + userId));
    }
}
