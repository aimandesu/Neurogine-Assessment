package org.example.namia.supportdeskspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.model.entity.Comment;
import org.example.namia.supportdeskspringboot.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    @Override
    public Comment Create(Comment entity) {
        return commentRepository.save(entity);
    }

    @Override
    public Optional<Comment> Get(UUID Id) {
        return Optional.empty();
    }

    @Override
    public List<Comment> GetAll() {
        return commentRepository.findAll().stream()
                .toList();
    }

    @Override
    public Comment Update(Comment entity) {
        return null;
    }

    @Override
    public void Delete(UUID Id) {

    }
}
