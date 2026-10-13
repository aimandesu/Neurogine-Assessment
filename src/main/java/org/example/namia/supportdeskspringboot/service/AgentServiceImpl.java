package org.example.namia.supportdeskspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.repository.AgentRepository;
import org.example.namia.supportdeskspringboot.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Override
    public Agent Create(Agent entity) {
        return null;
    }

    @Override
    public Optional<Agent> Get(UUID Id) {
        return agentRepository.findById(Id);
    }

    @Override
    public List<Agent> GetAll() {
        return List.of();
    }

    @Override
    public Agent Update(Agent entity) {
        return null;
    }

    @Override
    public void Delete(UUID Id) {

    }
}
