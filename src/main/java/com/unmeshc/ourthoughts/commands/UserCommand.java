package com.unmeshc.ourthoughts.commands;

import com.unmeshc.ourthoughts.validators.PasswordMatches;
import com.unmeshc.ourthoughts.validators.ValidEmail;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Created by uc on 9/29/2019
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class UserCommand {

    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @ValidEmail
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String matchingPassword;

    private Byte[] image;

    private LocalDateTime registrationDateTime;
}
