package com.unmeshc.ourthoughts.dtos;


import lombok.*;

/**
 * Created by uc on 10/9/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private Byte[] image;
}
