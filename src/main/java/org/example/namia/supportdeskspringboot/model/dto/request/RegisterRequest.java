package org.example.namia.supportdeskspringboot.model.dto.request;

import org.example.namia.supportdeskspringboot.model.enums.Roles;

public record RegisterRequest(String email, String password, Roles role) {}
