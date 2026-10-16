package org.example.namia.supportdeskspringboot.service;

import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.enums.Action;

import java.util.Optional;
import java.util.UUID;

public interface TicketService extends IBaseRepository<Ticket> {
    Optional<Ticket> AssignAgent(UUID TicketId, Agent Agent);
    Optional<Ticket> UnassignAgent(UUID TicketId, Agent Agent);
    Optional<Ticket> ReopenTicket(UUID TicketId, String Summary);
    public void UpdateTicketHistory(Ticket ticket, Action action);
}
