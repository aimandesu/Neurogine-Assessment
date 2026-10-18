package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public String originalFileName;
    public String storedFileName;
    public String contentType;
    public Integer size;
    public UUID uploadedBy;
    public OffsetDateTime uploadedDate = OffsetDateTime.now();


    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket ticket;

}
