package org.example.namia.supportdeskspringboot.model.dto.request;

import java.util.UUID;

public record CreateCommentRequest(
        UUID ticketId,
        String body
) {
}
