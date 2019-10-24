package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.commands.CommentCommand;
import com.unmeshc.ourthoughts.domain.Comment;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class CommentCommandToComment implements Converter<CommentCommand, Comment> {

    @Synchronized
    @Nullable
    @Override
    public Comment convert(CommentCommand source) {
        if (source == null) {
            return null;
        }

        final Comment target = Comment.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
