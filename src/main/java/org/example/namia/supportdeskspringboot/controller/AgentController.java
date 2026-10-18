package org.example.namia.supportdeskspringboot.controller;

import jakarta.validation.Valid;
import org.example.namia.supportdeskspringboot.exception.ResourceNotFoundException;
import org.example.namia.supportdeskspringboot.model.dto.mapper.AgentMapper;
import org.example.namia.supportdeskspringboot.model.dto.request.GetAllAgentRequest;
import org.example.namia.supportdeskspringboot.model.dto.request.UpdateAgentRequest;
import org.example.namia.supportdeskspringboot.model.dto.response.AgentDto;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.User;
import org.example.namia.supportdeskspringboot.model.enums.Roles;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.AgentService;
import org.example.namia.supportdeskspringboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;
    private final UserService userService;
    //MAPPER
    private final AgentMapper agentMapper;

    public AgentController(
            AgentService agentService,
            UserService userService,
            AgentMapper agentMapper
    ) {
        this.agentService = agentService;
        this.userService = userService;
        this.agentMapper = agentMapper;
    }

    @PostMapping()
    public ResponseEntity<Success<Agent>> registerAsAgent(
            Authentication authentication
    )
    {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "User not found"
                ));

        if (user.getRole() == Roles.Agent)
        {
            throw new IllegalStateException("User already an agent");
        }

        user.setRole(Roles.Agent);

        Agent agent = new Agent();
        agent.setEmail(user.getEmail());
        agent.setUser(user);

        Agent registeredAgent = agentService.Create(agent);

        return ResponseEntity.ok(new Success<>(
                registeredAgent,
                "User changed to Agent"
        ));

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Agent')")
    public ResponseEntity<Success<AgentDto>> updateAgent(
            @PathVariable UUID id,
            @Valid @RequestBody final UpdateAgentRequest request
    )
    {
        Agent agent = agentService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Agent not found"
                ));

        agent.setFullName(request.fullName());
        agent.setDepartment(request.department());
        agent.setEmail(request.email());
        agent.setActive(request.active());

        Agent updatedAgent = agentService.Update(agent);

        return ResponseEntity.ok(new Success<>(
                agentMapper.toDto(updatedAgent),
                "Agent updated"
        ));

    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Success<AgentDto>> activeDeactivateAgent(
            @PathVariable UUID id
    )
    {
        Agent agent = agentService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Agent not found"
                ));

        if (agent.isActive())
        {
            agentService.deactivateAgent(agent);
        }
        else
        {
            agent.setActive(true);
        }

        Agent updatedAgent = agentService.Update(agent);

        return ResponseEntity.ok(new Success<>(
                agentMapper.toDto(updatedAgent),
                "Agent active status updated"
        ));

    }

    @GetMapping()
    public ResponseEntity<Success<List<AgentDto>>> getAllAgents(
            @ModelAttribute GetAllAgentRequest request
    )
    {
        List<Agent> agents = agentService.getAllAgent(request);

        return ResponseEntity.ok(new Success<>(
                agentMapper.toDtoList(agents),
                "got all agents"
        ));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Success<AgentDto>> getAgent(
            @PathVariable UUID id
    )
    {
        Agent agent = agentService.Get(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOT FOUND",
                        "Agent not found"
                ));

        return ResponseEntity.ok(new Success<>(
                agentMapper.toDto(agent),
                "Got Agent"
        ));

    }

}
