package com.unmeshc.ourthoughts.commands;

import com.unmeshc.ourthoughts.validators.PasswordMatches;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Created by uc on 10/6/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatches
public class PasswordCommand {

    @NotBlank
    private String password;

    @NotBlank
    private String matchingPassword;
}
