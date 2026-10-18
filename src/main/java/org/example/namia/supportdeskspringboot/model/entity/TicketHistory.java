package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.namia.supportdeskspringboot.model.enums.Action;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "TicketHistory")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Enumerated(EnumType.STRING)
    public Action action;

    private OffsetDateTime timestamp = OffsetDateTime.now();


    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    public TicketHistory(
            User user,
            Ticket ticket,
            Action action) {
        this.user = user;
        this.ticket = ticket;
        this.action = action;
        this.timestamp = OffsetDateTime.now();
    }

}
