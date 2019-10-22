package com.unmeshc.ourthoughts.commands;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * Created by uc on 10/22/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommand {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private MultipartFile image;

    @NotBlank
    private String caption;

    private String writerName;
}
