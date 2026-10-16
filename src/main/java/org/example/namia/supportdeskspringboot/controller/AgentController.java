package org.example.namia.supportdeskspringboot.controller;

import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.shared.Success;
import org.example.namia.supportdeskspringboot.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping()
    public ResponseEntity<Success<List<Agent>>> getAllAgents()
    {
        List<Agent> agents = agentService.GetAll();

        return ResponseEntity.ok(new Success<>(
                agents,
                "got all agents"
        ));

    }

}
