package org.example.namia.supportdeskspringboot.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String description;
    private final Object customObject;

    //technically this is just a different class constructor that well,
    // we don't specify customObject
    public ResourceNotFoundException(String message, String description)
    {
        this(message, description, null);
    }

    public ResourceNotFoundException(String message, String description, Object customObject) {
        super(message);
        this.description = description;
        this.customObject = customObject;
    }
}
