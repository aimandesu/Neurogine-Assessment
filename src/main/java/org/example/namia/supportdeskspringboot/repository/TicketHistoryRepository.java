package org.example.namia.supportdeskspringboot.repository;

import org.example.namia.supportdeskspringboot.model.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, UUID> {
}
