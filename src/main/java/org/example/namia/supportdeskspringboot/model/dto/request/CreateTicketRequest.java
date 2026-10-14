package org.example.namia.supportdeskspringboot.model.dto.request;

import org.example.namia.supportdeskspringboot.model.enums.Priority;

public record CreateTicketRequest(
        String title,
        String description,
        String customerName,
        String customerEmail,
        Priority priority
) {}