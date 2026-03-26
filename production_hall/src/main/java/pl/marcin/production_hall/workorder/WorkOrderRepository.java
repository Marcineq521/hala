package pl.marcin.production_hall.workorder;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.production_hall.domain.WorkOrder;
import pl.marcin.production_hall.domain.WorkOrderStatus;

import java.util.List;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    List<WorkOrder> findAllByOrderByCreatedAtDesc();

    List<WorkOrder> findByMachine_IdOrderByCreatedAtDesc(UUID machineId);

    List<WorkOrder> findByStatusOrderByCreatedAtDesc(WorkOrderStatus status);
}
