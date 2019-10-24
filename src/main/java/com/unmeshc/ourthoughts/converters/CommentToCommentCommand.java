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
public class CommentToCommentCommand implements Converter<Comment, CommentCommand> {

    @Synchronized
    @Nullable
    @Override
    public CommentCommand convert(Comment source) {
        if (source == null) {
            return null;
        }

        final CommentCommand target = CommentCommand.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
