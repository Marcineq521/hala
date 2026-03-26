package pl.marcin.production_hall.workorder;

import lombok.Builder;
import pl.marcin.production_hall.domain.WorkOrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record WorkOrderResponse (
        UUID id,
        String orderNo,
        String title,
        WorkOrderStatus status,
        UUID machineId,
        String machineCode,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
){
}
