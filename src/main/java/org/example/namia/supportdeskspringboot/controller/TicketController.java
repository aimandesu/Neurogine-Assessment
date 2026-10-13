package org.example.namia.supportdeskspringboot.controller;

import jakarta.validation.Valid;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.input.UpdateTicketStatusRequest;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.AgentService;
import org.example.namia.supportdeskspringboot.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final AgentService agentService;


    public TicketController(TicketService ticketService, AgentService agentService) {
        this.ticketService = ticketService;
        this.agentService = agentService;
    }

    @PostMapping()
    public ResponseEntity<Success<Ticket>> createTicket(@RequestBody final Ticket ticket)
    {
        Ticket createdTicket = ticketService.Create(ticket);

        return ResponseEntity.ok(new Success<>(
                createdTicket,
                "Ticket created")
        );

    }

    @PutMapping("/{id}/assigner")
    public ResponseEntity<Success<Ticket>> assignerTicket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        Agent agent = agentService.Get(request.getAgentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "NOT FOUND",
                                "Agent not found"
                        )
                );

        Optional<Ticket> updatedTicket = switch (request.getAssignerOption()) {
            case Assign -> ticketService.AssignAgent(id, agent);
            case Unassign -> ticketService.UnassignAgent(id, agent);
        };

        if (updatedTicket.isEmpty()) {
            throw new ResourceNotFoundException(
                    "NOT FOUND",
                    "Ticket Not Found");
        }

        return ResponseEntity.ok(new Success<>(
                updatedTicket.get(),
                "Ticket Assigner successful"
        ));

    }

}
