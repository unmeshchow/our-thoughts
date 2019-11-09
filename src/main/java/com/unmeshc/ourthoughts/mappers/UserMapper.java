package com.unmeshc.ourthoughts.mappers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserAdminDto;
import com.unmeshc.ourthoughts.dtos.UserProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by uc on 11/9/2019
 */
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userCommandToUser(UserCommand userCommand);

    UserAdminDto userToUserAdminDto(User user);

    UserProfileDto userToUserProfileDto(User user);
}
