package org.example.namia.supportdeskspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.model.entity.TicketHistory;
import org.example.namia.supportdeskspringboot.repository.TicketHistoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketHistoryServiceImpl implements TicketHistoryService {

    private final TicketHistoryRepository ticketHistoryRepository;

    @Override
    public TicketHistory Create(TicketHistory entity) {
        return null;
    }

    @Override
    public Optional<TicketHistory> Get(UUID Id) {
        return Optional.empty();
    }

    @Override
    public List<TicketHistory> GetAll() {
        return ticketHistoryRepository.findAll().stream()
                .toList();
    }

    @Override
    public TicketHistory Update(TicketHistory entity) {
        return null;
    }

    @Override
    public void Delete(UUID Id) {

    }

    @Override
    public List<TicketHistory> GetAllByTicketId(UUID ticketId) {
        return ticketHistoryRepository.findByTicketId(
                ticketId,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );
    }
}
