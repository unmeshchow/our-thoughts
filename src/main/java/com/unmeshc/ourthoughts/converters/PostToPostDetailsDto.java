package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class PostToPostDetailsDto implements Converter<Post, PostDetailsDto> {

    @Synchronized
    @Nullable
    @Override
    public PostDetailsDto convert(Post source) {
        if (source == null) {
            return null;
        }

        final PostDetailsDto target = PostDetailsDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
