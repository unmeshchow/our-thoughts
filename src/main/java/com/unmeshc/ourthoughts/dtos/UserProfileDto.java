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
public class UserProfileDto {

    private String firstName;
    private String lastName;
    private String email;
    private boolean hasImage;
    private LocalDateTime registrationDateTime;
}
