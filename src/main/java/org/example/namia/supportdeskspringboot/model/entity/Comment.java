package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID Id;

    public String AuthorName;
    public String Body;
    public OffsetDateTime CreatedDate = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket Ticket;
}
