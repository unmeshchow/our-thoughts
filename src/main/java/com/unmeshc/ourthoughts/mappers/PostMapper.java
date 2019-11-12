package com.unmeshc.ourthoughts.mappers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.dtos.PostAdminDto;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by uc on 11/9/2019
 */
@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    Post postCommandToPost(PostCommand postCommand);

    PostAdminDto postToPostAdminDto(Post post);

    PostDetailsDto postToPostDetailsDto(Post post);

    PostSearchDto postToPostSearchDto(Post post);
}
