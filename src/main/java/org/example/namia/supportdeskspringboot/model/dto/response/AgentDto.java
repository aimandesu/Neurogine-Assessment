package org.example.namia.supportdeskspringboot.model.dto.response;

import org.example.namia.supportdeskspringboot.model.entity.User;
import org.example.namia.supportdeskspringboot.model.enums.Department;

import java.util.List;
import java.util.UUID;

public record AgentDto(
        UUID id,
        String fullName,
        Department department,
        String email,
        boolean active,
        List<TicketDto> tickets,
        User user
) {
}
