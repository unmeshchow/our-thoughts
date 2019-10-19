package com.unmeshc.ourthoughts.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by uc on 10/19/2019
 */
public interface ImageService {

    Byte[] convertIntoByteArray(MultipartFile imageFile);

    byte[] convertIntoByteArray(Byte[] bytes);
}
