package pl.marcin.production_hall.workorder;

import jakarta.validation.constraints.NotNull;
import pl.marcin.production_hall.domain.WorkOrderStatus;

public record UpdateWorkOrderStatusRequest(
        @NotNull WorkOrderStatus status
        ) {


}
