package org.example.namia.supportdeskspringboot.model.dto.mapper;

import org.example.namia.supportdeskspringboot.model.dto.response.AgentDto;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public interface AgentMapper {

    AgentDto toDto(Agent agent);

    List<AgentDto> toDtoList(List<Agent> agents);

}
