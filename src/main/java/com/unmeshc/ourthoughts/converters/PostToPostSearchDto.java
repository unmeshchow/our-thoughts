package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.dtos.PostSearchDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class PostToPostSearchDto implements Converter<Post, PostSearchDto > {

    @Synchronized
    @Nullable
    @Override
    public PostSearchDto convert(Post source) {
        if (source == null) {
            return null;
        }

        final PostSearchDto target = PostSearchDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
