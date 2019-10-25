package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by uc on 10/23/2019
 */
@Slf4j
@Component
public class ControllerUtils {

    public List<Post> adjustTitleAndBody(List<Post> posts) {

        posts.forEach(post -> {
            post.setTitle(addSpacesOrEllipsis(post.getTitle(), 15));
            post.setBody(addSpacesOrEllipsis(post.getBody(), 50));
        });

        return posts;
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
