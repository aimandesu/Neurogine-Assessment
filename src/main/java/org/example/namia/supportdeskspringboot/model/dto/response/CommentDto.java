package org.example.namia.supportdeskspringboot.model.dto.response;

import org.example.namia.supportdeskspringboot.model.enums.Visibilities;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        String authorName,
        String body,
        Visibilities visibility,
        OffsetDateTime createdDate
//        TicketDto ticket
) {
}
