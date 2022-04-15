package noroff.boxinatorapi.Repositories;

import noroff.boxinatorapi.Models.Shipment;
import noroff.boxinatorapi.Models.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    String findAllByShipmentStatus(ShipmentStatus shipmentStatus);
    boolean existsShipmentByReceiverName(String name);
}
