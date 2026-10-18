package org.example.namia.supportdeskspringboot.model.dto.request;

import org.example.namia.supportdeskspringboot.model.enums.Department;

public record UpdateAgentRequest(
        String fullName,
        Department department,
        String email,
        boolean active
) {
}
