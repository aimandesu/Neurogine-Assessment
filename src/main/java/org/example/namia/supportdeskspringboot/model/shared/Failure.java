package org.example.namia.supportdeskspringboot.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Failure<T>(
        String message,
        String description,
        Object customObject
) implements ResultResponse<T> {
}