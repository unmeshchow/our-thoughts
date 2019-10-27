package com.unmeshc.ourthoughts.dtos;

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
public class PostDetailsCommentDto {

    private String message;
    private LocalDateTime addingDateTime;
    private Long userId;
}
