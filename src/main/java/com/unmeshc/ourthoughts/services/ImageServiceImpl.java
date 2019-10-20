package com.unmeshc.ourthoughts.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Override
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

    @Override
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
}
