package org.example.namia.supportdeskspringboot.controller;

import jakarta.validation.Valid;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.dto.mapper.CommentMapper;
import org.example.namia.supportdeskspringboot.model.dto.mapper.TicketHistoryMapper;
import org.example.namia.supportdeskspringboot.model.dto.mapper.TicketMapper;
import org.example.namia.supportdeskspringboot.model.dto.request.CreateCommentRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.CreateUpdateTicketRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.UpdateStatusRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.UpdateTicketStatusRequest;
import org.example.namia.supportdeskspringboot.model.dto.response.CommentDto;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketDto;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketHistoryDto;
import org.example.namia.supportdeskspringboot.model.entity.*;
import org.example.namia.supportdeskspringboot.model.enums.Action;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CommentService commentService;
    private final TicketHistoryService ticketHistoryService;
    private final AgentService agentService;
    //MAPPER
    private final TicketMapper ticketMapper;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final CommentMapper commentMapper;


    public TicketController(
            TicketService ticketService,
            CommentService commentService,
            TicketHistoryService ticketHistoryService,
            AgentService agentService,
            TicketMapper ticketMapper,
            TicketHistoryMapper ticketHistoryMapper,
            CommentMapper commentMapper
    ) {
        this.ticketService = ticketService;
        this.commentService = commentService;
        this.ticketHistoryService = ticketHistoryService;
        this.agentService = agentService;
        this.ticketMapper = ticketMapper;
        this.ticketHistoryMapper = ticketHistoryMapper;
        this.commentMapper = commentMapper;
    }

    @PostMapping()
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Success<Ticket>> createTicket(
            @RequestBody final CreateUpdateTicketRequest request)
    {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCustomerName(request.customerName());
        ticket.setCustomerEmail(request.customerEmail());
        ticket.setPriority(request.priority());
        ticket.setTicketStatus(TStatus.New);

        Ticket createdTicket = ticketService.Create(ticket);

        return ResponseEntity.ok(new Success<>(
                createdTicket,
                "Ticket created")
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<Success<TicketDto>> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUpdateTicketRequest request)
    {

        Ticket ticket = ticketService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Ticket not found"
                ));

        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCustomerName(request.customerName());
        ticket.setCustomerEmail(request.customerEmail());
        ticket.setPriority(request.priority());

        Ticket updatedTicket = ticketService.Update(ticket);

        if (ticket.getPriority() != request.priority())
        {
            ticketService.UpdateTicketHistory(
                    updatedTicket, Action.PriorityChanged);
        }

        return ResponseEntity.ok(new Success<>(
                ticketMapper.toDto(updatedTicket),
                "Ticket updated"
        ));

    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Success<List<TicketHistoryDto>>> getTicketHistory(
            @PathVariable UUID id
    )
    {
        List<TicketHistory> ticketHistories = ticketHistoryService
                .GetAllByTicketId(id);

        List<TicketHistoryDto> ticketHistoryDto = ticketHistoryMapper.toDtoList(ticketHistories);

        return ResponseEntity.ok(new Success<>(
                ticketHistoryDto,
                "got all ticket histories"
        ));

    }

    // CHANGE TO PAGINATION
    @GetMapping()
    public ResponseEntity<Success<List<TicketDto>>> getAllTicket()
    {
        List<Ticket> tickets = ticketService.GetAll();

        List<TicketDto> ticketDto = ticketMapper.toDtoList(tickets);

        return ResponseEntity.ok(new Success<>(
                ticketDto,
                "got all tickets"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Success<TicketDto>> getTicket(
            @PathVariable UUID Id
    )
    {
        Ticket ticket = ticketService.Get(Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Ticket not found"
                ));

        return ResponseEntity.ok(new Success<>(
                ticketMapper.toDto(ticket),
                "Ticket found"
        ));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Success<?>> deleteTicket(
            @PathVariable UUID id
    )
    {
        ticketService.Delete(id);

        return ResponseEntity.ok(new Success<>(
                "",
                "Ticket deleted"
        ));

    }

    @PutMapping("/{id}/assignee")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Success<TicketDto>> assignTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        Agent agent = agentService.Get(request.getAgentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "NOT FOUND",
                                "Agent not found"
                        )
                );

//        Optional<Ticket> updatedTicket = switch (request.getAssignerOption()) {
//            case Assign -> ticketService.AssignAgent(id, agent);
//            case Unassign -> ticketService.UnassignAgent(id, agent);
//        };

        Optional<Ticket> updatedTicket = ticketService.AssignAgent(id, agent);

        if (updatedTicket.isEmpty()) {
            throw new ResourceNotFoundException(
                    "NOT FOUND",
                    "Ticket Not Found");
        }

        return ResponseEntity.ok(new Success<>(
                ticketMapper.toDto(updatedTicket.get()),
                "Ticket Assigner successful"
        ));

    }

    @DeleteMapping("/{id}/assignee")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Success<TicketDto>> unassignTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {

        Agent agent = agentService.Get(request.getAgentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "NOT FOUND",
                                "Agent not found"
                        )
                );

        Ticket updatedTicket = ticketService.UnassignAgent(id, agent)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Ticket to unassign not found"
                ));

        return ResponseEntity.ok(new Success<>(
                ticketMapper.toDto(updatedTicket),
                "Ticket unassign successful"
        ));

    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Success<TicketDto>> UpdateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request
    )
    {

        Ticket ticket = ticketService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Ticket not found"
                ));

        ticket.setResolutionSummary(request.resolutionSummary());
        ticket.updateTicketStatus(request.status());

        Ticket updatedTicket = ticketService.Update(ticket);

        if (ticket.getTicketStatus() != request.status())
        {
            ticketService.UpdateTicketHistory(
                    updatedTicket, Action.StatusChanged);
        }

        return ResponseEntity.ok(new Success<>(
                ticketMapper.toDto(updatedTicket),
                "Ticket status changed"
        ));

    }

    //COMMENTS
    @PostMapping("/{id}/comments")
    public ResponseEntity<Success<CommentDto>> createComment(
            @PathVariable UUID id,
            @RequestBody final CreateCommentRequest request,
            Authentication authentication)
    {
        Ticket ticket = ticketService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Ticket not found"
                ));

        Comment comment = new Comment();

        comment.setTicket(ticket);
        comment.setBody(request.body());
        comment.setAuthorName(authentication.getName());

        Comment passedComment = ticket.AddComment(comment);

        commentService.Create(passedComment);

        CommentDto commentDto = commentMapper.toDto(passedComment);

        return ResponseEntity.ok(new Success<>(
                commentDto,
                "Comment added to the ticket"
        ));

    }

}
