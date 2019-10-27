package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.dtos.PostAdminDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class PostToPostAdminDto implements Converter<Post, PostAdminDto> {

    @Synchronized
    @Nullable
    @Override
    public PostAdminDto convert(Post source) {
        if (source == null) {
            return null;
        }

        final PostAdminDto target = PostAdminDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
