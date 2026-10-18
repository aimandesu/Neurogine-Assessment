package org.example.namia.supportdeskspringboot.controller;

import org.example.namia.supportdeskspringboot.model.dto.mapper.CommentMapper;
import org.example.namia.supportdeskspringboot.model.dto.response.CommentDto;
import org.example.namia.supportdeskspringboot.model.entity.Comment;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.CommentService;
import org.example.namia.supportdeskspringboot.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;
    private final CommentMapper commentMapper;

    public CommentController(
            CommentService commentService,
            TicketService ticketService, CommentMapper commentMapper
    ) {
        this.commentService = commentService;
        this.ticketService = ticketService;
        this.commentMapper = commentMapper;
    }

    @GetMapping()
    public ResponseEntity<Success<List<CommentDto>>> getAllComment()
    {
        List<Comment> comments = commentService.GetAll();

        List<CommentDto> commentDto = commentMapper.toDtoList(comments);

//        List<CommentDto> commentDto = comments.stream().map(
//                comment -> {
//
//                    Ticket ticket = comment.getTicket();
//
//                    return new CommentDto(
//                            comment.getId(),
//                            comment.getAuthorName(),
//                            comment.getBody(),
//                            comment.getVisibility(),
//                            comment.getCreatedDate(),
//                            ticket
//                    );
//
//                }
//        ).toList();

        return ResponseEntity.ok(new Success<>(
                commentDto,
                "got all comments"
        ));

    }

}
