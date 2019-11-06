package com.unmeshc.ourthoughts.dtos;

import lombok.*;

/**
 * Created by uc on 10/27/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostSearchDto {

    private Long id; // post id
    private String title;
    private String body;
}
