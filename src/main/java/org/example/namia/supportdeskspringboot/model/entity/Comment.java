package org.example.namia.supportdeskspringboot.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.example.namia.supportdeskspringboot.model.enums.Visibilities;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public String authorName;
    public String body;

    @Enumerated(EnumType.STRING)
    public Visibilities visibility = Visibilities.Internal;

    public OffsetDateTime createdDate = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "TicketId")
    private Ticket ticket;
}

/* NOTE FOR COMMENT - TICKET
*   @ManyToOne
    @JoinColumn(name = "TicketId")
    @JsonBackReference //Tell Jackson to not serialize this
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "Id"
    ) //Tell Jackson to trace this object by its "id" property
    @JsonIdentityReference(
            alwaysAsId = true
    ) //Tell Jackson to ONLY output the ID value, not the whole object
    private Ticket ticket;
*
* */
