package org.example.namia.supportdeskspringboot.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Success<T>(
        T data,
        String description
) implements  ResultResponse<T> {
}
