package com.unmeshc.ourthoughts.commands;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by uc on 10/22/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostCommand {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private MultipartFile photo;

    private LocalDateTime creationDateTime;

    @NotBlank
    private String caption;

    private String writerName;

    private List<CommentCommand> commentCommands;
}
