package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.namia.supportdeskspringboot.model.enums.Department;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "Agent")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public String fullName;

    @Enumerated(EnumType.STRING)
    public Department department;

    @Column(unique = true, nullable = false)
    public String email;

    public boolean active = true;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "AssignedAgent")
    public List<Ticket> tickets = new ArrayList<>(); //what's difference using public and private here

    @OneToOne
    @JoinColumn(name = "UserId")
    private User user;
}
