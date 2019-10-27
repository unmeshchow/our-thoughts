package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.dtos.CommentDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class CommentToCommentDto implements Converter<Comment, CommentDto> {

    @Synchronized
    @Nullable
    @Override
    public CommentDto convert(Comment source) {
        if (source == null) {
            return null;
        }

        final CommentDto target = CommentDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
