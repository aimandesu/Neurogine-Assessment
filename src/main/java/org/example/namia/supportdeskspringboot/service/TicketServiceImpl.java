package org.example.namia.supportdeskspringboot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.entity.TicketHistory;
import org.example.namia.supportdeskspringboot.model.entity.User;
import org.example.namia.supportdeskspringboot.model.enums.Action;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import org.example.namia.supportdeskspringboot.repository.TicketHistoryRepository;
import org.example.namia.supportdeskspringboot.repository.TicketRepository;
import org.example.namia.supportdeskspringboot.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Ticket Create(Ticket entity) {
        entity.setTicketStatus(TStatus.New);

        Ticket ticket = ticketRepository.saveAndFlush(entity); // INSERT now, counter read back
        ticket.setReference();

        UpdateTicketHistory(ticket, Action.Created);

        return ticketRepository.save(ticket);                  // explicit UPDATE of Reference
    }

    @Override
    public Optional<Ticket> Get(UUID Id) {
        return ticketRepository.findById(Id);
    }

    @Override
    public List<Ticket> GetAll() {
//        return StreamSupport.stream(ticketRepository.findAll().spliterator(), false)
//                .toList();
        return ticketRepository.findAll().stream()
                .filter(t -> !t.getIsDeleted())
                .toList();
    }

    @Override
    public Ticket Update(Ticket entity) {

        UpdateTicketHistory(entity, Action.Updated);

        return ticketRepository.save(entity);
    }

    @Override
    public void Delete(UUID Id) {
        Ticket ticket = Get(Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket.NOT_FOUND",
                        "Ticket not found")
                );

        ticket.setIsDeleted(true);

        Update(ticket);
        UpdateTicketHistory(ticket, Action.Deleted);

    }

    @Override
    public Optional<Ticket> AssignAgent(UUID TicketId, Agent Agent) {
        Optional<Ticket> ticket = Get(TicketId);

        if (ticket.isEmpty())
        {
            return Optional.empty();
        }

        Ticket searchedTicket = ticket.get();

        searchedTicket.AssignAgent(Agent);

        Update(searchedTicket);
        UpdateTicketHistory(searchedTicket, Action.Assigned);

        return Optional.of(searchedTicket);
    }

    @Override
    public Optional<Ticket> UnassignAgent(UUID TicketId, Agent Agent) {

        Ticket ticket = Get(TicketId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Ticket.NOT_FOUND",
                        "Ticket not found"
                )
        );

        if (ticket.getTicketStatus() == TStatus.InProgress)
        {
            throw new IllegalStateException("Ticket is in progress. Please assign ticket to other agent instead");
        }

        ticket.UnassignAgent(Agent);

        Update(ticket);
        UpdateTicketHistory(ticket, Action.Unassigned);

        return Optional.of(ticket);

    }

    @Override
    public Optional<Ticket> ReopenTicket(UUID TicketId, String Summary) {

        Ticket ticket = Get(TicketId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Ticket.NOT_FOUND",
                        "Ticket not found"
                )
        );

        ticket.setResolutionSummary(Summary);
        ticket.updateTicketStatus(TStatus.InProgress);

        Update(ticket);
        UpdateTicketHistory(ticket, Action.Commented);

        return Optional.of(ticket);
    }

    @Override
    public void UpdateTicketHistory(Ticket ticket, Action action) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User loggedInUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User.NOT_FOUND", "User not found"));

        TicketHistory history = new TicketHistory(
                loggedInUser,
                ticket,
                action
        );

        ticketHistoryRepository.save(history);
    }

}
