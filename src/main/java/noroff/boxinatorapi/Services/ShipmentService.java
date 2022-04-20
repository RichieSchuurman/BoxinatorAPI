package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.*;
import noroff.boxinatorapi.Repositories.CountryRepository;
import noroff.boxinatorapi.Repositories.ShipmentRepository;
import noroff.boxinatorapi.Utilities.Command;
import noroff.boxinatorapi.Utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private AccountService accountService;

    public ResponseEntity<CommonResponse> getAllShipments(HttpServletRequest request, Jwt principal) {
        String userRole = principal.getClaimAsStringList("roles").get(0);
        List<Shipment> shipments;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();

        if (AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
            shipments = shipmentRepository.findAll();
        } else {
            Account sender = accountService.getAccountByJwt(principal);
            shipments = shipmentRepository.findAllBySender(sender);
        }

        commonResponse.data = shipments;
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

        shipment = shipmentRepository.save(shipment);
        commonResponse.data = shipment;
        commonResponse.message = "New shipment added, with id: " + shipment.getId();
        resp = HttpStatus.CREATED;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getShipmentById(HttpServletRequest request, Long id) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = shipmentRepository.findById(id);
        commonResponse.message = "All shipments";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    //TODO
    //GET /shipments/customer/:customer_id

    public ResponseEntity<CommonResponse> updateShipment(HttpServletRequest request, Long id, Shipment updatedShipment) {
        Shipment shipment;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (shipmentRepository.existsById(id)) {
            Optional<Shipment> shipmentRepos = shipmentRepository.findById(id);
            shipment = shipmentRepos.get();

            if (updatedShipment.getReceiverName() != null) {
                shipment.setReceiverName(updatedShipment.getReceiverName());
            }

            if (updatedShipment.getWeightOption() != 0) {
                shipment.setWeightOption(updatedShipment.getWeightOption());
            }

            if (updatedShipment.getBoxColor() != null) {
                shipment.setBoxColor(updatedShipment.getBoxColor());
            }

            if (updatedShipment.getDestinationCountry() != null) {
                shipment.setDestinationCountry(updatedShipment.getDestinationCountry());
            }

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

    public ResponseEntity<CommonResponse> deleteShipment(HttpServletRequest request, Long id) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (shipmentRepository.existsById(id)) {
            shipmentRepository.deleteById(id);
            commonResponse.message = "Deleted shipment with id: " + id;
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
