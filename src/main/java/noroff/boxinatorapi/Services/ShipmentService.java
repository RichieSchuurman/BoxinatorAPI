package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Models.Country;
import noroff.boxinatorapi.Models.Shipment;
import noroff.boxinatorapi.Models.ShipmentStatus;
import noroff.boxinatorapi.Repositories.ShipmentRepository;
import noroff.boxinatorapi.Utilities.Command;
import noroff.boxinatorapi.Utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    public ResponseEntity<CommonResponse> getAllShipments(HttpServletRequest request) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = shipmentRepository.findAll();
        commonResponse.message = "All shipments";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);

    }

    public ResponseEntity<CommonResponse> getCompletedShipments(HttpServletRequest request, ShipmentStatus shipmentStatus) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
        commonResponse.message = "All completed shipments";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getCanceledShipments(HttpServletRequest request, ShipmentStatus shipmentStatus) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
        commonResponse.message = "All canceled shipments";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> addShipment(HttpServletRequest request, Shipment shipment) {
        Command cmd = new Command(request);
        HttpStatus resp;

        CommonResponse commonResponse = new CommonResponse();

        if (shipmentRepository.existsShipmentByReceiverName(shipment.getReceiverName())) {
            commonResponse.data = null;
            commonResponse.message = "The country " + shipment.getReceiverName() + " has already been added";
            resp = HttpStatus.BAD_REQUEST;
        } else {
            shipment = shipmentRepository.save(shipment);
            commonResponse.data = shipment;
            commonResponse.message = "New country added, with id: " + shipment.getId();
            resp = HttpStatus.CREATED;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getAllShipmentsById(HttpServletRequest request, Long id) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = shipmentRepository.findAll();
        commonResponse.message = "All shipments";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);

    }

    public ResponseEntity<CommonResponse> updateShipment(HttpServletRequest request, Long id) {
        Shipment shipment;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (shipmentRepository.existsById(id)) {
            Optional<Shipment> shipmentRepos = shipmentRepository.findById(id);
            shipment = shipmentRepos.get();

            shipment = shipmentRepository.save(shipment);

            commonResponse.data = shipment;
            commonResponse.message = "Updated shipment with id: " + shipment.getId();
            resp = HttpStatus.OK;
        } else {
            commonResponse.data = null;
            commonResponse.message = "Shipment with id " + id + " not found";
            resp = HttpStatus.NOT_FOUND;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }
}
