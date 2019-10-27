package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class UserToUserDto implements Converter<User, UserDto> {

    @Synchronized
    @Nullable
    @Override
    public UserDto convert(User source) {
        if (source == null) {
            return null;
        }

        final UserDto target = UserDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
