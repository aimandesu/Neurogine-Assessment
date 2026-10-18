package org.example.namia.supportdeskspringboot.model.dto.mapper;

import org.example.namia.supportdeskspringboot.model.dto.response.CommentDto;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketDto;
import org.example.namia.supportdeskspringboot.model.entity.Comment;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);

    /*
    * What if a Comment really needs to map its parent Ticket?
    *
    * If your CommentDto has a field like TicketDto ticket inside it,
    * and you absolutely must map it backwards, you will create an infinite loop
    * where Ticket maps Comment, which maps Ticket, which maps Comment.
    *
    * To break this cycle, you must use MapStruct's @Context cycle tracking
    * or create a specialized, shallow mapping method that ignores the relationship fields
    * using @Mapping(target = "comments", ignore = true).
    * */

}
