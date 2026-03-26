package pl.marcin.production_hall.eventlog;


import java.time.OffsetDateTime;
import java.util.UUID;

public record EventLogResponse(
        UUID id,
        OffsetDateTime createdAt,
        EventLogType type,
        UUID machineId,
        UUID assignmentId,
        UUID workOrderId,
        String message
) {
}
