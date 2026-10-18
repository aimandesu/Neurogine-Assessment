package org.example.namia.supportdeskspringboot.model.dto.request;

import org.example.namia.supportdeskspringboot.model.enums.Department;

public record GetAllAgentRequest(
        String email,
        String name,
        Department department,
        Boolean isActive
) {
}
