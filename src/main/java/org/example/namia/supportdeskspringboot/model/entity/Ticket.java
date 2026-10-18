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

    private Priority Priority;

    @NonNull
    private TStatus TicketStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID Id;

    @Column(name = "TicketSeq", insertable = false, updatable = false,
            columnDefinition = "bigint generated always as identity")
    @org.hibernate.annotations.Generated
    private Long TicketSeq;

    @Column(unique = true)
    private String Reference;

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

    private Long ReopenCount;

    public String ResolutionSummary;
    public String Version;
    public Boolean IsDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "ticket")
    private List<Comment> Comments = new ArrayList<>();

    /* NOTE FOR COMMENT - TICKET
    *   @OneToMany(mappedBy = "ticket")
        @JsonManagedReference
        private List<Comment> Comments = new ArrayList<>();
    *
    * */

    @OneToMany
    private List<Attachment> Attachments = new ArrayList<>();

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
        if (this.TicketStatus == TStatus.Resolved || this.TicketStatus == TStatus.Closed)
        {
            throw new IllegalStateException("Can't set priority when Ticket is Resolved" +
                    "Or Ticket is Closed");
        }

        this.Priority = priority;
        this.recalculateOverdueDate();
    }

    @PostPersist
    public void setReference()
    {
        this.Reference = String.format(
                "TCK-%d-%04d",
                this.CreatedDate.getYear(),
                this.TicketSeq);
    }

    private void setReopenCount(Long ReopenCount)
    {
        if (this.ReopenCount > 3)
        {
            throw new IllegalStateException("Can't reopen ticket more than 3 times");
        }

        this.ReopenCount = ReopenCount + 1L;
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

            if (this.ResolutionSummary.isEmpty())
            {
                throw new IllegalStateException("Resolution Summary is not provided. Please Provide.");
            }

            this.ResolvedDate = OffsetDateTime.now();
        }

        if (this.TicketStatus == TStatus.Closed)
        {
            this.ClosedDate = OffsetDateTime.now();
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

        AssignedAgent = agent.id;

    }

    public void UnassignAgent(Agent agent)
    {
        if (AssignedAgent != agent.id)
        {
            throw new IllegalStateException(String.format(
                    "Cannot unassign inactive agent: %s to a ticket that doesnt belong to this agent.",
                    agent.id
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
            this.setReopenCount(this.getReopenCount());
        }
    }

}
