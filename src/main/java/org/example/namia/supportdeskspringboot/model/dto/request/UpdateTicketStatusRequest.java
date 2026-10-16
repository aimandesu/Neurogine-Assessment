package org.example.namia.supportdeskspringboot.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.namia.supportdeskspringboot.model.enums.AssignerOption;

import java.util.UUID;

@Data
public class UpdateTicketStatusRequest {

    @NotNull(message = "Agent Id is required")
    private UUID AgentId;

    @NotNull(message = "Option is required")
    private AssignerOption AssignerOption;

}
