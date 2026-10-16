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
    public UUID Id;

    @Enumerated(EnumType.STRING)
    public Action Action;

    private OffsetDateTime Timestamp = OffsetDateTime.now();


    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket Ticket;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User User;

    public TicketHistory(
            User user,
            Ticket ticket,
            Action action) {
        this.User = user;
        this.Ticket = ticket;
        this.Action = action;
        this.Timestamp = OffsetDateTime.now();
    }

}
