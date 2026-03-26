package pl.marcin.production_hall.eventlog;


import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-logs")
@RequiredArgsConstructor
public class EventLogController {
    private final EventLogService eventLogService;

    @PostMapping
    public EventLogResponse create(@RequestBody @Valid CreateEventLogRequest request){
        return eventLogService.create(request);
    }

    @GetMapping
    public List<EventLogResponse> getAll(){
        return eventLogService.getAll();
    }

    @GetMapping("/machine/{machineId}")
    public List<EventLogResponse> getByMachine(@PathVariable UUID machineId){
        return eventLogService.getByMachine(machineId);
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<EventLogResponse> getByAssignment(@PathVariable UUID assignmentId){
        return eventLogService.getByAssignment(assignmentId);
    }

    @GetMapping("/work-order/{workOrderId}")
    public List<EventLogResponse> getByWorkOrder(@PathVariable UUID workOrderId){
        return eventLogService.getByWorkOrder(workOrderId);
    }
}
