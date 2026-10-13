package org.example.namia.supportdeskspringboot.repository;

import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgentRepository extends CrudRepository<Agent, UUID> {
}
