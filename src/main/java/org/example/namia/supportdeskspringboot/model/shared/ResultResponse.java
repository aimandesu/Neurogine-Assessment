package org.example.namia.supportdeskspringboot.model.shared;

public sealed interface ResultResponse<T> permits Success, Failure {

    static <T> ResultResponse<T> Success(T data, String description) {
        return new Success<>(data, description);
    }

    static <T> ResultResponse<T> Error(String message, String description, Object customObject) {
        return new Failure<>(message, description, customObject);
    }
}
