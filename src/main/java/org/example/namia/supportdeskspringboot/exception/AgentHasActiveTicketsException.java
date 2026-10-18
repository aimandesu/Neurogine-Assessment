package org.example.namia.supportdeskspringboot.exception;

import lombok.Getter;
import org.example.namia.supportdeskspringboot.model.dto.response.TicketDto;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;

import java.util.List;

@Getter
public class AgentHasActiveTicketsException extends RuntimeException {
    private final List<TicketDto> blockingTickets;

    public AgentHasActiveTicketsException(List<TicketDto> blockingTickets) {
        super("Cannot deactivate agent because they have active tickets assigned.");
        this.blockingTickets = blockingTickets;
    }

}
