package com.unmeshc.ourthoughts.commands;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by uc on 10/24/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentCommand {

    private Long id;

    private String message;

    private Long userId;

    private Long postId;

    private LocalDateTime addingDateTime;
}
