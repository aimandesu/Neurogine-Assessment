package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.namia.supportdeskspringboot.model.enums.Priority;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "Ticket")
public class Ticket {

    private Priority priority;

    @NonNull
    private TStatus ticketStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "TicketSeq", insertable = false, updatable = false,
            columnDefinition = "bigint generated always as identity")
    @org.hibernate.annotations.Generated
    private Long TicketSeq;

    @Column(unique = true)
    private String reference;

    public String title;
    public String description;
    public String customerName;
    public String customerEmail;

    @Setter(AccessLevel.PRIVATE)
    private UUID assignedAgent;

    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime lastModifiedDate = OffsetDateTime.now();
    public OffsetDateTime resolvedDate;
    public OffsetDateTime closedDate;

    @Setter(AccessLevel.PRIVATE)
    private OffsetDateTime dueDate;

    private Long reopenCount = 0L;

    public String resolutionSummary;
    public String version;
    public Boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "ticket")
    private List<Comment> comments = new ArrayList<>();

    /* NOTE FOR COMMENT - TICKET
    *   @OneToMany(mappedBy = "ticket")
        @JsonManagedReference
        private List<Comment> Comments = new ArrayList<>();
    *
    * */

    @OneToMany
    private List<Attachment> attachments = new ArrayList<>();

    public boolean isReadOnly() {
        return this.ticketStatus == TStatus.Closed;
    }

    // We don't need this because Lombok has auto set for this
    // by using @NonNull
//    public Ticket(TicketStatus ticketStatus)
//    {
//        if (ticketStatus == null) {
//            throw new IllegalArgumentException("TStatus is required");
//        }
//        this.ticketStatus = ticketStatus;
//    }


    public void setPriority(Priority priority)
    {
        if (this.ticketStatus == TStatus.Resolved || this.ticketStatus == TStatus.Closed)
        {
            throw new IllegalStateException("Can't set priority when Ticket is Resolved" +
                    "Or Ticket is Closed");
        }

        this.priority = priority;
        this.recalculateOverdueDate();
    }

    @PostPersist
    public void setReference()
    {
        this.reference = String.format(
                "TCK-%d-%04d",
                this.createdDate.getYear(),
                this.TicketSeq);
    }

    private void setReopenCount(Long ReopenCount)
    {
        if (ReopenCount > 3)
        {
            throw new IllegalStateException("Can't reopen ticket more than 3 times");
        }

        this.reopenCount = ReopenCount;
    }


    private void recalculateOverdueDate()
    {
        if (this.ticketStatus == TStatus.Closed
                || this.ticketStatus == TStatus.Resolved)
        {
            return;
        }

        this.dueDate = switch (this.priority) {
            case Low -> this.createdDate.plusDays(7);
            case Normal -> this.createdDate.plusDays(3);
            case High -> this.createdDate.plusDays(1);
            case Critical -> this.createdDate.plusHours(4);
        };

    }

    public void updateTicketStatus(TStatus newStatus)
    {
        if (this.ticketStatus == newStatus)
        {
            throw new IllegalStateException("Could not change to same status");
        }

        if (this.isReadOnly())
        {
            throw new IllegalStateException("Cannot change status: " +
                    "This ticket is closed and read-only.");
        }

        var isValid = validateStatusTransition(this.ticketStatus, newStatus);

        if (!isValid)
        {
            throw new IllegalStateException(String.format(
                    "Invalid status transition from %s to %s. Either because agent not assigned yet or status not following flow",
                    this.ticketStatus, newStatus
            ));
        }

        this.ticketStatus = newStatus;

        if (this.ticketStatus == TStatus.Resolved)
        {

            if (this.resolutionSummary.isEmpty())
            {
                throw new IllegalStateException("Resolution Summary is not provided. Please Provide.");
            }

            this.resolvedDate = OffsetDateTime.now();
        }

        if (this.ticketStatus == TStatus.Closed)
        {
            this.closedDate = OffsetDateTime.now();
        }

    }

    public void AssignAgent(Agent agent)
    {
        if (!agent.active)
        {
            throw new IllegalStateException(String.format(
                    "Cannot assign inactive agent: %s to a ticket",
                    agent.id
            ));
        }

        assignedAgent = agent.id;

    }

    public void UnassignAgent(Agent agent)
    {
        if (assignedAgent != agent.id)
        {
            throw new IllegalStateException(String.format(
                    "Cannot unassign inactive agent: %s to a ticket that doesnt belong to this agent.",
                    agent.id
            ));
        }

        assignedAgent = null;
    }

    public Comment AddComment(Comment comment)
    {
        if (this.isReadOnly())
        {
            throw new IllegalStateException(
                    "Cannot change status: This ticket is closed and read-only."
            );
        }

        return comment;
    }

    public boolean isOverdue(){
        return OffsetDateTime.now().isAfter(this.dueDate)
                && this.ticketStatus != TStatus.Resolved
                && this.ticketStatus != TStatus.Closed;
    }

    public boolean validateStatusTransition(TStatus currentStatus, TStatus newStatus) {

        if (currentStatus == TStatus.New && newStatus == TStatus.InProgress) {
            return this.getAssignedAgent() != null;
        }

        if (currentStatus == TStatus.InProgress && newStatus == TStatus.Resolved) {
            return true;
        }

        if (currentStatus == TStatus.Resolved &&
                (newStatus == TStatus.Closed || newStatus == TStatus.InProgress)
        ) {

            reopeningTicket(currentStatus, newStatus);

            return true;
        }

        return false;
    }

    public void reopeningTicket(TStatus currentStatus, TStatus newStatus)
    {
        if ( currentStatus == TStatus.Resolved
                && newStatus == TStatus.InProgress)
        {
            long currentCount = (this.reopenCount == null) ? 0L : this.reopenCount;

            this.setReopenCount(currentCount + 1L);

        }
    }

}
