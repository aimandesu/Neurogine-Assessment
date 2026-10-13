package org.example.namia.supportdeskspringboot.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.namia.supportdeskspringboot.model.enums.AssignerOption;
import org.example.namia.supportdeskspringboot.model.enums.TStatus;

import java.util.UUID;

@Data
public class UpdateTicketStatusRequest {

    @NotNull(message = "Agent Id is required")
    private UUID AgentId;

    @NotBlank(message = "Option is invalid")
    private AssignerOption AssignerOption;

}
