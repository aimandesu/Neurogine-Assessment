package org.example.namia.supportdeskspringboot.service;

import org.example.namia.supportdeskspringboot.model.entity.TicketHistory;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface TicketHistoryService extends IBaseRepository<TicketHistory>{
    List<TicketHistory> GetAllByTicketId(UUID ticketId);
}
