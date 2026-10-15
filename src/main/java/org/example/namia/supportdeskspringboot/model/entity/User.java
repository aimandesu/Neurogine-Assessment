package org.example.namia.supportdeskspringboot.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.namia.supportdeskspringboot.model.enums.Roles;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID Id;

    @Column(unique = true, nullable = false)
    public String Email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    public Roles Role;

    public Boolean Enabled = true;
    public OffsetDateTime CreatedDate = OffsetDateTime.now();

}
