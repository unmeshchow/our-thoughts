package com.unmeshc.ourthoughts.converters;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import lombok.Synchronized;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/2/2019
 */
@Component
public class PostToPostCommand implements Converter<Post, PostCommand> {

    @Synchronized
    @Nullable
    @Override
    public PostCommand convert(Post source) {
        if (source == null) {
            return null;
        }

        final PostCommand target = PostCommand.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
