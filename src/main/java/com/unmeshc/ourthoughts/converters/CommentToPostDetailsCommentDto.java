package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.dtos.PostDetailsCommentDto;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class CommentToPostDetailsCommentDto implements Converter<Comment, PostDetailsCommentDto> {

    @Synchronized
    @Nullable
    @Override
    public PostDetailsCommentDto convert(Comment source) {
        if (source == null) {
            return null;
        }

        final PostDetailsCommentDto target = PostDetailsCommentDto.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
