package org.example.namia.supportdeskspringboot.model.dto.response;

import org.example.namia.supportdeskspringboot.model.entity.User;

public record JwtResponse(
        String token,
        String type,
        User user) {
    public JwtResponse(String token, User user) {
        this(token, "Bearer", user);
    }
}
