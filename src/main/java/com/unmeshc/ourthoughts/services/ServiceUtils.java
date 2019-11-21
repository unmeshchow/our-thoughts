package com.unmeshc.ourthoughts.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by uc on 11/11/2019
 */
@Slf4j
@Component
public class ServiceUtils {

    public byte[] convertIntoByteArray(MultipartFile imageFile) {
        try {
            return imageFile.getBytes();
        } catch (Exception exc) {
            log.error("Error occurred during converting image into Byte[]", exc);
            throw new RuntimeException("Error occurred in converting image, try again.");
        }
    }

    public String addSpacesOrEllipsis(String text, int length) {
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
