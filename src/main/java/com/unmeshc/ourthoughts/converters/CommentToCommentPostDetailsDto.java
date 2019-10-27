package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.dtos.CommentPostDetailsDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class CommentToCommentPostDetailsDto implements Converter<Comment, CommentPostDetailsDto> {

    @Synchronized
    @Nullable
    @Override
    public CommentPostDetailsDto convert(Comment source) {
        if (source == null) {
            return null;
        }

        final CommentPostDetailsDto target = CommentPostDetailsDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
