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
public class UserPostAdminDto {

    private Long id; // user id
    private String firstName;
    private List<PostAdminDto> postAdminDtos;
}
