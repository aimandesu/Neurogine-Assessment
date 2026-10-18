package org.example.namia.supportdeskspringboot.model.dto.request;

import org.example.namia.supportdeskspringboot.model.enums.TStatus;

public record UpdateStatusRequest(
        TStatus status,
        String resolutionSummary
//        String version, whats the use of this?

) {
}
