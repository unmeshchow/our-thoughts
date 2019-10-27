package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.converters.PostToPostDto;
import com.unmeshc.ourthoughts.converters.UserToUserDto;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostDto;
import com.unmeshc.ourthoughts.dtos.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uc on 10/23/2019
 */
@Slf4j
@Component
public class ControllerUtils {

    private final UserToUserDto userToUserDto;
    private final PostToPostDto postToPostDto;

    public ControllerUtils(UserToUserDto userToUserDto,
                           PostToPostDto postToPostDto) {
        this.userToUserDto = userToUserDto;
        this.postToPostDto = postToPostDto;
    }

    public List<PostDto> convertToPostDtoList(List<Post> posts) {
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDto = postToPostDto.convert(post);
            postDtos.add(postDto);
        });

        return postDtos;
    }

    public List<UserDto> convertToUserDtoList(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(user -> {
            UserDto userDto = userToUserDto.convert(user);
            userDtos.add(userDto);
        });

        return userDtos;
    }

    public List<PostDto> adjustTitleAndBody(List<PostDto> postDtos) {

        postDtos.forEach(post -> {
            post.setTitle(addSpacesOrEllipsis(post.getTitle(), 15));
            post.setBody(addSpacesOrEllipsis(post.getBody(), 50));
        });

        return postDtos;
    }

    public void copyBytesToResponse(HttpServletResponse response, byte[] bytes) {
        response.setContentType("image/jpeg");
        InputStream inputStream = new ByteArrayInputStream((bytes));

        try {
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception exc) {
            log.error("Error occurred during copying input stream into output stream");
            throw new RuntimeException("Error occurred in retrieving image, try again.");
        }
    }

    public boolean isNotCorrectUserImage(MultipartFile multipartFile) {
        return isNull(multipartFile) ||
               isNotJpegType(multipartFile) ||
               isNotWithin100KB(multipartFile);
    }

    public boolean isNotCorrectPostPhoto(MultipartFile multipartFile) {
        return isNull(multipartFile) ||
                isNotJpegType(multipartFile) ||
                isNotWithin300KB(multipartFile);
    }

    public void adjustPagination(Page<?> postPage, PageTracker pageTracker) {

        // Calculate start and end page for the dynamic pagination
        if (postPage.getTotalPages() < pageTracker.getEndPage()) {
            pageTracker.setEndPage(postPage.getTotalPages());

        } else if (pageTracker.getStartPage() != 1 &&
                  (pageTracker.getCurrentPage() == pageTracker.getStartPage() ||
                          pageTracker.getCurrentPage() == (pageTracker.getStartPage() + 1))) {

            pageTracker.setStartPage(pageTracker.getStartPage() - 1);
            pageTracker.setEndPage(pageTracker.getEndPage() - 1);

        } else if (pageTracker.getEndPage() != postPage.getTotalPages() &&
                  (pageTracker.getCurrentPage() == pageTracker.getEndPage() ||
                          (pageTracker.getCurrentPage() == (pageTracker.getEndPage() -1)))) {

            pageTracker.setStartPage(pageTracker.getStartPage() + 1);
            pageTracker.setEndPage(pageTracker.getEndPage() + 1);
        }
    }

    private boolean isNull(MultipartFile multipartFile) {
        return multipartFile == null;
    }

    private boolean isNotJpegType(MultipartFile multipartFile) {
        return !"image/jpeg".equals(multipartFile.getContentType());
    }

    private boolean isNotWithin100KB(MultipartFile multipartFile) {
        return multipartFile.getSize() > 100000;
    }

    private boolean isNotWithin300KB(MultipartFile multipartFile) {
        return multipartFile.getSize() > 300000;
    }

    private static String addSpacesOrEllipsis(String text, int length) {
        int textLength = text.length();
        if (textLength == length) {
            return text;
        } else if (textLength < length) {
          int difference = length - textLength;
          for (int i = 0; i < difference; i++ ) {
              text += " ";
          }
          return text;
        }

        return text.substring(0, length - 3) + "...";
    }
}
