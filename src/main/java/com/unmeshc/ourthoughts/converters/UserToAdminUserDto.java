package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.AdminUserDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class UserToAdminUserDto implements Converter<User, AdminUserDto> {

    @Synchronized
    @Nullable
    @Override
    public AdminUserDto convert(User source) {
        if (source == null) {
            return null;
        }

        final AdminUserDto target = AdminUserDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
