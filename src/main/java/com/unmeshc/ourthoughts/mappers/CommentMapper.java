package com.unmeshc.ourthoughts.mappers;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.dtos.CommentAdminDto;
import com.unmeshc.ourthoughts.dtos.CommentPostDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by uc on 11/9/2019
 */
@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentAdminDto commentToCommentAdminDto(Comment comment);

    CommentPostDetailsDto commentToCommentPostDetailsDto(Comment comment);
}
