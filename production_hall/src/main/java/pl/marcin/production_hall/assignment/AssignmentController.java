package pl.marcin.production_hall.assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("/active")
    public List<AssignmentResponse> getActiveAssignments(){
        return assignmentService.getActiveAssignments();
    }

    @GetMapping("/machine/{machineId}")
    public List<AssignmentResponse> getAssignmentsForMachine(@PathVariable UUID machineId){
        return assignmentService.getAssignmentsForMachine(machineId);
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse startAssignment(@RequestBody StartAssignmentRequest request){
        return assignmentService.startAssignment(request.getMachineId(),request.getOperatorName(),request.getWorkOrderId());
    }

    @PostMapping("/end")
    public AssignmentResponse endAssignment(@RequestBody EndAssignmentRequest request){
        return assignmentService.endAssignment(request.getMachineId());
    }
}
