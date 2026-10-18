package org.example.namia.supportdeskspringboot.repository;

import org.example.namia.supportdeskspringboot.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
