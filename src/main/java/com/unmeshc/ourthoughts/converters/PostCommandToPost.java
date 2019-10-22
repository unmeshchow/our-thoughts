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
public class PostCommandToPost implements Converter<PostCommand, Post> {

    @Synchronized
    @Nullable
    @Override
    public Post convert(PostCommand source) {
        if (source == null) {
            return null;
        }

        final Post target = Post.builder().build();
        BeanUtils.copyProperties(source, target);

        return target;
    }
}
