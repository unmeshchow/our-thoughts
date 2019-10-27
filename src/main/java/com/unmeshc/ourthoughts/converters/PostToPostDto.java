package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.dtos.PostDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class PostToPostDto implements Converter<Post, PostDto> {

    @Synchronized
    @Nullable
    @Override
    public PostDto convert(Post source) {
        if (source == null) {
            return null;
        }

        final PostDto target = PostDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
