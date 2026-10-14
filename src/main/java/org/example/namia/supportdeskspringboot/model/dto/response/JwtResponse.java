package org.example.namia.supportdeskspringboot.model.dto.response;

public record JwtResponse(String token, String type) {
    public JwtResponse(String token) {
        this(token, "Bearer");
    }
}
