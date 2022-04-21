package noroff.boxinatorapi.Repositories;

import noroff.boxinatorapi.Models.Account;
import noroff.boxinatorapi.Models.Shipment;
import noroff.boxinatorapi.Models.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByShipmentStatus(ShipmentStatus shipmentStatus);
    List<Shipment> findAllBySenderAndShipmentStatus(Account sender, ShipmentStatus shipmentStatus);
    List<Shipment> findAllBySender(Account sender);
}
