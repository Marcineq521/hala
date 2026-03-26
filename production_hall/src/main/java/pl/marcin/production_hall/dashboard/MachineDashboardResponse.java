package pl.marcin.production_hall.dashboard;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MachineDashboardResponse(
        UUID machineId,
        String machineCode,
        String machineName,
        boolean active,
        boolean occupied,
        String operatorName,
        OffsetDateTime assignmentStartTime,
        UUID workOrderId,
        String workOrderNo,
        String workOrderTitle,
        String workOrderStatus
) {

}
