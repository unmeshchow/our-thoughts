package com.unmeshc.ourthoughts.dtos;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by uc on 10/27/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentAdminDto {

    private Long id;
    private String message;
    private LocalDateTime addingDateTime;
}
