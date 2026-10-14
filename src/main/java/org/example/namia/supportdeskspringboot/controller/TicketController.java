package org.example.namia.supportdeskspringboot.controller;

import jakarta.validation.Valid;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.dto.request.CreateTicketRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.UpdateTicketStatusRequest;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.AgentService;
import org.example.namia.supportdeskspringboot.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<Success<Ticket>> createTicket(@RequestBody final CreateTicketRequest request)
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

    @GetMapping()
    public ResponseEntity<Success<List<Ticket>>> getAllTicket()
    {
        List<Ticket> tickets = ticketService.GetAll();

        return ResponseEntity.ok(new Success<>(
                tickets,
                "got all tickets"
        ));
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
