package org.example.namia.supportdeskspringboot.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.example.namia.supportdeskspringboot.model.enums.Priority;

public record CreateUpdateTicketRequest(
        String title,
        String description,
        String customerName,
        String customerEmail,
        @NotBlank Priority priority
) {}