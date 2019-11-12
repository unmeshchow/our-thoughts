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

    public Byte[] convertIntoByteArray(MultipartFile imageFile) {
        try {
            Byte[] bytes = new Byte[imageFile.getBytes().length];
            int i =0;
            for (byte b : imageFile.getBytes()) {
                bytes[i++] = b;
            }

            return bytes;
        } catch (Exception exc) {
            log.error("Error occurred during converting image into Byte[]", exc);
            throw new RuntimeException("Error occurred in converting image, try again.");
        }
    }

    public byte[] convertIntoByteArray(Byte[] bytes) {
        try {
            byte[] theBytes = new byte[bytes.length];
            int i = 0;

            for (Byte b : bytes) {
                theBytes[i++] = b.byteValue();
            }

            return theBytes;
        } catch (Exception exc) {
            log.error("Error occurred during converting into byte[]", exc);
            throw new RuntimeException("Error occurred in converting to byte, try again.");
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
