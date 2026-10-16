package org.example.namia.supportdeskspringboot.service;

import org.example.namia.supportdeskspringboot.model.entity.User;

import java.util.Optional;

public interface UserService extends IBaseRepository<User>{
    public Optional<User> findByEmail(String email);
}
