package org.example.namia.supportdeskspringboot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import org.example.namia.supportdeskspringboot.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public Ticket Create(Ticket entity) {
        entity.setTicketStatus(TStatus.New);

        Ticket saved = ticketRepository.saveAndFlush(entity); // INSERT now, counter read back
        saved.setReference();

        return ticketRepository.save(saved);                  // explicit UPDATE of Reference
    }

    @Override
    public Optional<Ticket> Get(UUID Id) {
        return ticketRepository.findById(Id);
    }

    @Override
    public List<Ticket> GetAll() {
        return StreamSupport.stream(ticketRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public Ticket Update(Ticket entity) {
        Ticket existingTicket = Get(entity.Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket.NOT_FOUND",
                        "Ticket not found")
                );

        return ticketRepository.save(existingTicket);
    }

    @Override
    public void Delete(UUID Id) {

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

        return Optional.of(searchedTicket);
    }

    @Override
    public Optional<Ticket> UnassignAgent(UUID TicketId, Agent Agent) {
        return Optional.empty();
    }

}
