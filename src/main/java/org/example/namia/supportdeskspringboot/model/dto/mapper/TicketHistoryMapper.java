package org.example.namia.supportdeskspringboot.model.dto.mapper;

import org.example.namia.supportdeskspringboot.model.dto.response.CommentDto;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketDto;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketHistoryDto;
import org.example.namia.supportdeskspringboot.model.entity.Comment;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.entity.TicketHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketHistoryMapper {
    TicketHistoryDto toDto(TicketHistory ticketHistory);
    List<TicketHistoryDto> toDtoList(List<TicketHistory> ticketHistories);


//    @Mapping(target = "comments", ignore = true)
    TicketDto toDto(Ticket ticket);

    CommentDto toCommentDto(Comment comment);
}

