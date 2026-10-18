package org.example.namia.supportdeskspringboot.repository;

import jakarta.persistence.criteria.Predicate;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.dto.request.GetAllAgentRequest;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class AgentSpecifications {

    public static Specification<Agent> filterAgents(GetAllAgentRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter by Email (using case-insensitive partial match 'LIKE')
            if (request.email() != null && !request.email().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + request.email().toLowerCase() + "%"
                ));
            }

            // 2. Filter by Name (using case-insensitive partial match 'LIKE')
            if (request.name() != null && !request.name().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")),
                        "%" + request.name().toLowerCase() + "%"
                ));
            }

            // 3. Filter by Department (Exact match)
            if (request.department() != null) {
                predicates.add(criteriaBuilder.equal(root.get("department"), request.department()));
            }

            // 4. Filter by IsActive (Exact match)
            if (request.isActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), request.isActive()));
            }

            // Combine all active filters with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
