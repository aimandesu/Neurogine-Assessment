package org.example.namia.supportdeskspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.exception.AgentHasActiveTicketsException;
import org.example.namia.supportdeskspringboot.model.dto.mapper.TicketMapper;
import org.example.namia.supportdeskspringboot.model.dto.request.GetAllAgentRequest;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.Ticket;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;
import org.example.namia.supportdeskspringboot.repository.AgentRepository;
import org.example.namia.supportdeskspringboot.repository.AgentSpecifications;
import org.example.namia.supportdeskspringboot.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final TicketRepository ticketRepository;

    //MAPPER
    private final TicketMapper ticketMapper;

    @Override
    public Agent Create(Agent entity) {
        return agentRepository.save(entity);
    }

    @Override
    public Optional<Agent> Get(UUID Id) {
        return agentRepository.findById(Id);
    }

    @Override
    public List<Agent> GetAll() {
        return StreamSupport.stream(agentRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public Agent Update(Agent entity) {
        return agentRepository.save(entity);
    }

    @Override
    public void Delete(UUID Id) {

    }

    @Override
    public void deactivateAgent(Agent agent) {

//        List<Ticket> blockingTickets = ticketRepository.findAll().stream()
//                .filter(t -> t.getAssignedAgent() != null
//                        && t.getAssignedAgent().equals(agent.id))
//                .filter(t -> t.getTicketStatus() == TStatus.New
//                        || t.getTicketStatus() == TStatus.InProgress)
//                .toList();

        List<Ticket> blockingTickets = ticketRepository.findByAssignedAgentAndTicketStatusIn(
                agent.id,
                List.of(TStatus.New, TStatus.InProgress)
        );


        if (!blockingTickets.isEmpty()) {
            throw new AgentHasActiveTicketsException(ticketMapper.toDtoList(blockingTickets));
        }

        agent.setActive(false);
        agentRepository.save(agent);

    }

    @Override
    public List<Agent> getAllAgent(GetAllAgentRequest request) {
        return agentRepository.findAll(AgentSpecifications.filterAgents(request));
    }


}
