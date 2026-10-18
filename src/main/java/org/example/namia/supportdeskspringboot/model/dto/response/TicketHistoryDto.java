package org.example.namia.supportdeskspringboot.model.dto.response;

import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.enums.Action;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketHistoryDto(
        UUID id,
        Action action,
        OffsetDateTime timestamp,
        TicketDto ticket
) {}
