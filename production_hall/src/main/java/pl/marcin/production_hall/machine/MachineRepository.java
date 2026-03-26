package pl.marcin.production_hall.machine;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.production_hall.domain.Machine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface MachineRepository extends JpaRepository<Machine, UUID> {
    Optional<Machine> findByCode(String code);
    List<Machine> findAllByOrderByCodeAsc();
}
