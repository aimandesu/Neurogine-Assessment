package org.example.namia.supportdeskspringboot.service;

import org.example.namia.supportdeskspringboot.model.dto.request.GetAllAgentRequest;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentService extends IBaseRepository<Agent>{
    public void deactivateAgent(Agent agent);
    public List<Agent> getAllAgent(GetAllAgentRequest request);
}
