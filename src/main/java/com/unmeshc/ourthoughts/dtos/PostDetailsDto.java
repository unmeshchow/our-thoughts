package com.unmeshc.ourthoughts.dtos;

import lombok.*;

import java.util.List;

/**
 * Created by uc on 10/27/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostDetailsDto {

    private Long id; // post id
    private String title;
    private String body;
    private String caption;
    private String writerName;
    private List<CommentPostDetailsDto> postDetailsCommentDtos;
}
