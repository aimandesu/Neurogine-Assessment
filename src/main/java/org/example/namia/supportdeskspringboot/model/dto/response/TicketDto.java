package org.example.namia.supportdeskspringboot.model.dto.response;

import org.example.namia.supportdeskspringboot.model.enums.Priority;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TicketDto(
        UUID id,
        String title,
        String description,
        String customerName,
        String customerEmail,
        Priority priority,
        TStatus ticketStatus,
        UUID assignedAgent,
        String reference,
        OffsetDateTime createdDate,
        OffsetDateTime dueDate,
        List<CommentDto> comments
) {}
