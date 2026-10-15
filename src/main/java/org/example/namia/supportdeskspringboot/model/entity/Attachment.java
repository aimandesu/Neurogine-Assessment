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
    public UUID Id;

    public String OriginalFileName;
    public String StoredFileName;
    public String ContentType;
    public Integer Size;
    public UUID UploadedBy;
    public OffsetDateTime UploadedDate = OffsetDateTime.now();


    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket Ticket;

}
