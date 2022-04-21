package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.*;
import noroff.boxinatorapi.Repositories.AccountRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CountryRepository countryRepository;

    public ResponseEntity<CommonResponse> getAllCurrentShipments(HttpServletRequest request, Jwt principal) {
        List<Shipment> currentShipments = new ArrayList<>();
        String userRole = principal.getClaimAsStringList("roles").get(0);
        Account currentUser = accountRepository.getById(principal.getSubject());
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();

        if (AccountType.valueOf(userRole).equals(AccountType.REGISTERED_USER)) {
            // Only add shipments that are non-completed and non-cancelled
            for (ShipmentStatus shipmentStatus: ShipmentStatus.values()) {
                if (shipmentStatus.equals(ShipmentStatus.CREATED) || shipmentStatus.equals(ShipmentStatus.IN_TRANSIT) || shipmentStatus.equals(ShipmentStatus.RECEIVED)) {
                    List<Shipment> currentShipmentsBySender = shipmentRepository.findAllBySenderAndShipmentStatus(currentUser, shipmentStatus);
                    currentShipments.addAll(currentShipmentsBySender);
                }
            }
        } else if (AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
            for (ShipmentStatus shipmentStatus: ShipmentStatus.values()) {
                if (shipmentStatus.equals(ShipmentStatus.CREATED) || shipmentStatus.equals(ShipmentStatus.IN_TRANSIT) || shipmentStatus.equals(ShipmentStatus.RECEIVED)) {
                    List<Shipment> allCurrentShipments = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
                    currentShipments.addAll(allCurrentShipments);
                }
            }
        }

        commonResponse.data = currentShipments;
        commonResponse.message = "All current shipments (non-cancelled/non-completed)";
        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getShipmentById(HttpServletRequest request, Long id, Jwt principal) {
        Shipment shipment;
        String userId = principal.getClaimAsString("sub");
        String userRole = principal.getClaimAsStringList("roles").get(0);
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!shipmentRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Shipment not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            Optional<Shipment> shipmentRepos = shipmentRepository.findById(id);
            shipment = shipmentRepos.get();

            if (shipment.getSender().getId().equals(userId) || AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
                commonResponse.data = shipment;
                commonResponse.message = "Found shipment with id: " + shipment.getId();
                resp = HttpStatus.OK;
            } else {
                commonResponse.data = null;
                commonResponse.message = "You can only view your own shipments";
                resp = HttpStatus.FORBIDDEN;
            }
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getShipmentsByStatus(
            HttpServletRequest request,
            ShipmentStatus shipmentStatus,
            Jwt principal) {
        List<Shipment> shipmentsByStatus = new ArrayList<>();
        String userRole = principal.getClaimAsStringList("roles").get(0);
        Account currentUser = accountRepository.getById(principal.getSubject());
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();

        if (AccountType.valueOf(userRole).equals(AccountType.REGISTERED_USER)) {
            shipmentsByStatus = shipmentRepository.findAllBySenderAndShipmentStatus(currentUser, shipmentStatus);
        } else if (AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
            shipmentsByStatus = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
        }
        commonResponse.data = shipmentsByStatus;
        commonResponse.message = "All shipments (completed or cancelled)";
        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getShipmentsByCustomer(HttpServletRequest request, String id) {
        Account customer;
        List<Shipment> shipmentsByCustomer;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!accountRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Customer and shipments not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            Optional<Account> accountRepos = accountRepository.findById(id);
            customer = accountRepos.get();
            shipmentsByCustomer = shipmentRepository.findAllBySender(customer);
            commonResponse.data = shipmentsByCustomer;
            commonResponse.message = "Found shipments of customer with id: " + id;
            resp = HttpStatus.OK;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> addShipment(HttpServletRequest request, Shipment shipment, Jwt principal) {
        String userId = principal.getClaimAsString("sub");
        Account sender = accountRepository.getById(userId);
        Command cmd = new Command(request);
        HttpStatus resp;
        CommonResponse commonResponse = new CommonResponse();

        shipment.setShipmentStatus(ShipmentStatus.CREATED);
        shipment.setSender(sender);

        Country destinationCountry = countryRepository.getCountryByName(shipment.getDestinationCountry().getName());
        shipment.setDestinationCountry(destinationCountry);


        shipment = shipmentRepository.save(shipment);
        commonResponse.data = shipment;
        commonResponse.message = "New shipment added, with id: " + shipment.getId();
        resp = HttpStatus.CREATED;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> updateShipment(HttpServletRequest request, Long id, Shipment updatedShipment, Jwt principal) {
        Shipment shipment;
        String userId = principal.getClaimAsString("sub");
        String userRole = principal.getClaimAsStringList("roles").get(0);
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!shipmentRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Shipment not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            Optional<Shipment> shipmentRepos = shipmentRepository.findById(id);
            shipment = shipmentRepos.get();
            if (shipment.getSender().getId().equals(userId) || AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
                // Any non-Administrator users may only cancel a shipment
                if (AccountType.valueOf(userRole).equals(AccountType.REGISTERED_USER)) {
                    shipment.setShipmentStatus(ShipmentStatus.CANCELED);
                } else {
                    // An administrator can make any changes they wish to a shipment
                    if (updatedShipment.getReceiverName() != null) {
                        shipment.setReceiverName(updatedShipment.getReceiverName());
                    }
                    if (updatedShipment.getWeightOption() != null) {
                        shipment.setWeightOption(updatedShipment.getWeightOption());
                    }
                    if (updatedShipment.getBoxColor() != null) {
                        shipment.setBoxColor(updatedShipment.getBoxColor());
                    }
                    if (updatedShipment.getShipmentStatus() != null) {
                        shipment.setShipmentStatus(updatedShipment.getShipmentStatus());
                    }
                    if (updatedShipment.getSender() != null) {
                        Account sender = accountRepository.getById(updatedShipment.getSender().getId());
                        shipment.setSender(sender);
                    }
                    if (updatedShipment.getDestinationCountry() != null) {
                        Country destinationCountry = countryRepository.getCountryByName(updatedShipment.getDestinationCountry().getName());
                        shipment.setDestinationCountry(destinationCountry);
                    }
                }
                shipment = shipmentRepository.save(shipment);
                commonResponse.data = shipment;
                commonResponse.message = "Updated shipment with id: " + id;
                resp = HttpStatus.OK;
            } else {
                commonResponse.data = null;
                commonResponse.message = "You can only update your own shipments";
                resp = HttpStatus.FORBIDDEN;
            }
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> deleteShipment(HttpServletRequest request, Long id) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!shipmentRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Shipment with id " + id + " not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            shipmentRepository.deleteById(id);
            resp = HttpStatus.NO_CONTENT;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }



//    public ResponseEntity<CommonResponse> getAllShipments(HttpServletRequest request, Jwt principal) {
//        String userRole = principal.getClaimAsStringList("roles").get(0);
//        List<Shipment> shipments;
//        Command cmd = new Command(request);
//        CommonResponse commonResponse = new CommonResponse();
//
//        if (AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
//            shipments = shipmentRepository.findAll();
//        } else {
//            Account sender = accountService.getAccountByJwt(principal);
//            shipments = shipmentRepository.findAllBySender(sender);
//        }
//
//        commonResponse.data = shipments;
//        commonResponse.message = "All shipments";
//        HttpStatus resp = HttpStatus.OK;
//
//        cmd.setResult(resp);
//        Logger.getInstance().logCommand(cmd);
//        return new ResponseEntity<>(commonResponse, resp);
//    }
//
//    public ResponseEntity<CommonResponse> getCompletedShipments(HttpServletRequest request, ShipmentStatus shipmentStatus) {
//        Command cmd = new Command(request);
//
//        CommonResponse commonResponse = new CommonResponse();
//        commonResponse.data = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
//        commonResponse.message = "All completed shipments";
//
//        HttpStatus resp = HttpStatus.OK;
//
//        cmd.setResult(resp);
//        Logger.getInstance().logCommand(cmd);
//        return new ResponseEntity<>(commonResponse, resp);
//    }
//
//    public ResponseEntity<CommonResponse> getCanceledShipments(HttpServletRequest request, ShipmentStatus shipmentStatus) {
//        Command cmd = new Command(request);
//
//        CommonResponse commonResponse = new CommonResponse();
//        commonResponse.data = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
//        commonResponse.message = "All canceled shipments";
//
//        HttpStatus resp = HttpStatus.OK;
//
//        cmd.setResult(resp);
//        Logger.getInstance().logCommand(cmd);
//        return new ResponseEntity<>(commonResponse, resp);
//    }
//
//
//    public ResponseEntity<CommonResponse> getShipmentById(HttpServletRequest request, Long id) {
//        Command cmd = new Command(request);
//
//        CommonResponse commonResponse = new CommonResponse();
//        commonResponse.data = shipmentRepository.findById(id);
//        commonResponse.message = "All shipments";
//
//        HttpStatus resp = HttpStatus.OK;
//
//        cmd.setResult(resp);
//        Logger.getInstance().logCommand(cmd);
//        return new ResponseEntity<>(commonResponse, resp);
//    }
//
//    //TODO
//    //GET /shipments/customer/:customer_id
//
//    public ResponseEntity<CommonResponse> updateShipment(HttpServletRequest request, Long id, Shipment updatedShipment) {
//        Shipment shipment;
//        Command cmd = new Command(request);
//        CommonResponse commonResponse = new CommonResponse();
//        HttpStatus resp;
//
//        if (shipmentRepository.existsById(id)) {
//            Optional<Shipment> shipmentRepos = shipmentRepository.findById(id);
//            shipment = shipmentRepos.get();
//
//            if (updatedShipment.getReceiverName() != null) {
//                shipment.setReceiverName(updatedShipment.getReceiverName());
//            }
//
//            if (updatedShipment.getWeightOption() != 0) {
//                shipment.setWeightOption(updatedShipment.getWeightOption());
//            }
//
//            if (updatedShipment.getBoxColor() != null) {
//                shipment.setBoxColor(updatedShipment.getBoxColor());
//            }
//
//            if (updatedShipment.getDestinationCountry() != null) {
//                shipment.setDestinationCountry(updatedShipment.getDestinationCountry());
//            }
//
//            shipment = shipmentRepository.save(shipment);
//
//            commonResponse.data = shipment;
//            commonResponse.message = "Updated shipment with id: " + shipment.getId();
//            resp = HttpStatus.OK;
//        } else {
//            commonResponse.data = null;
//            commonResponse.message = "Shipment with id " + id + " not found";
//            resp = HttpStatus.NOT_FOUND;
//        }
//
//        cmd.setResult(resp);
//        Logger.getInstance().logCommand(cmd);
//        return new ResponseEntity<>(commonResponse, resp);
//    }
//
}
