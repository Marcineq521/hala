package pl.marcin.production_hall.workorder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.marcin.production_hall.domain.WorkOrderStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    public WorkOrderResponse create(@RequestBody @Valid CreateWorkOrderRequest request) {
        return workOrderService.create(request);
    }

    @GetMapping
    public List<WorkOrderResponse> getAll() {
        return workOrderService.getAll();
    }

    @GetMapping("/machine/{machineId}")
    public List<WorkOrderResponse> getByMachine(@PathVariable UUID machineId) {
        return workOrderService.getByMachine(machineId);
    }

    @PatchMapping("/{workOrderId}/status")
    public WorkOrderResponse updateStatus(
            @PathVariable UUID workOrderId,
            @RequestBody @Valid UpdateWorkOrderStatusRequest request
    ) {
        return workOrderService.updateStatus(workOrderId, request.status());
    }
}