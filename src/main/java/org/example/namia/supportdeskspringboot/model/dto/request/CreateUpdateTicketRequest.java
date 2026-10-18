package org.example.namia.supportdeskspringboot.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.namia.supportdeskspringboot.model.enums.Priority;

public record CreateUpdateTicketRequest(
        String title,
        String description,
        String customerName,
        String customerEmail,
        @NotNull Priority priority
) {}