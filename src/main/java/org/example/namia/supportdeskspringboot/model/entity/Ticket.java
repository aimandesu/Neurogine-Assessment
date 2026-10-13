package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.example.namia.supportdeskspringboot.model.enums.Priority;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "Ticket")
public class Ticket {

    private Priority Priority;

    @NonNull
    private TStatus TicketStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID Id;

    public String Reference;
    public String Title;
    public String Description;
    public String CustomerName;
    public String CustomerEmail;

    @Setter(AccessLevel.PRIVATE)
    private UUID AssignedAgent;

    private OffsetDateTime CreatedDate = OffsetDateTime.now();
    private OffsetDateTime LastModifiedDate = OffsetDateTime.now();
    public OffsetDateTime ResolvedDate;
    public OffsetDateTime ClosedDate;

    @Setter(AccessLevel.PRIVATE)
    private OffsetDateTime DueDate;

    @OneToMany
    private List<Comment> Comments = new ArrayList<>();

    public boolean isReadOnly() {
        return this.TicketStatus == TStatus.Closed;
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
        this.Priority = priority;
        this.recalculateOverdueDate();
    }



    private void recalculateOverdueDate()
    {
        if (this.TicketStatus == TStatus.Closed
                || this.TicketStatus == TStatus.Resolved)
        {
            return;
        }

        this.DueDate = switch (this.Priority) {
            case Low -> this.CreatedDate.plusDays(7);
            case Normal -> this.CreatedDate.plusDays(3);
            case High -> this.CreatedDate.plusDays(1);
            case Critical -> this.CreatedDate.plusHours(4);
        };

    }

    public void updateTicketStatus(TStatus newStatus)
    {
        if (this.TicketStatus == newStatus) return;

        if (this.isReadOnly())
        {
            throw new IllegalStateException("Cannot change status: " +
                    "This ticket is closed and read-only.");
        }

        var isValid = validateStatusTransition(this.TicketStatus, newStatus);

        if (!isValid)
        {
            throw new IllegalStateException(String.format(
                    "Invalid status transition from %s to %s. Either because agent not assigned yet or status not following flow",
                    this.TicketStatus, newStatus
            ));
        }

        this.TicketStatus = newStatus;

        if (this.TicketStatus == TStatus.Resolved)
        {
            this.ResolvedDate = OffsetDateTime.now();
        }

        if (this.TicketStatus == TStatus.Closed)
        {
            this.ClosedDate = OffsetDateTime.now();
        }

    }

    public void AssignAgent(Agent agent)
    {
        if (!agent.Active)
        {
            throw new IllegalStateException(String.format(
                    "Cannot assign inactive agent: %s to a ticket",
                    agent.Id
            ));
        }

        AssignedAgent = agent.Id;

    }

    public void UnassignAgent(Agent agent)
    {
        if (AssignedAgent != agent.Id)
        {
            throw new IllegalStateException(String.format(
                    "Cannot unassign inactive agent: %s to a ticket that doesnt belong to this agent.",
                    agent.Id
            ));
        }

        AssignedAgent = null;
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
        return OffsetDateTime.now().isAfter(this.DueDate)
                && this.TicketStatus != TStatus.Resolved
                && this.TicketStatus != TStatus.Closed;
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
            return true;
        }

        return false;
    }

}
