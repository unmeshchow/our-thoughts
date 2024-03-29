package com.unmeshc.ourthoughts.dtos;

import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * Created by uc on 10/27/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostCommentAdminDto {

    private Long userId;
    private Long id; // post id
    private String title;
    private int currentPage;
    private Set<Integer> pageNumbers;
    private List<CommentAdminDto> commentAdminDtos;
}
