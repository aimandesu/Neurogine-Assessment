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
    public UUID Id;

    public String FullName;
    public Department Department;
    public boolean Active = true;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TickedId")
    public List<Ticket> tickets = new ArrayList<>();; //what's difference using public and private here
}
