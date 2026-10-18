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

    TicketDto toTicketDto(Ticket ticket);

}
