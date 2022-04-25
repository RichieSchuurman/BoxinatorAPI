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

    @Autowired
    private AccountService accountService;

    public ResponseEntity<CommonResponse> getAllCurrentShipments(HttpServletRequest request, Jwt principal) {
        List<Shipment> currentShipments = new ArrayList<>();
        Account currentUser = accountService.getAccountFromJwt(principal);
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();

        if (currentUser.getAccountType().equals(AccountType.REGISTERED_USER)) {
            // Only add shipments that are non-completed and non-cancelled
            for (ShipmentStatus shipmentStatus: ShipmentStatus.values()) {
                if (shipmentStatus.equals(ShipmentStatus.CREATED) || shipmentStatus.equals(ShipmentStatus.IN_TRANSIT) || shipmentStatus.equals(ShipmentStatus.RECEIVED)) {
                    List<Shipment> currentShipmentsBySender = shipmentRepository.findAllBySenderAndShipmentStatus(currentUser, shipmentStatus);
                    currentShipments.addAll(currentShipmentsBySender);
                }
            }
        } else if (currentUser.getAccountType().equals(AccountType.ADMINISTRATOR)) {
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
        Account currentUser = accountService.getAccountFromJwt(principal);
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

            boolean ownsShipment = shipment.getSender().getKeyCloakUserId().equals(currentUser.getKeyCloakUserId());
            boolean hasAdminRole = currentUser.getAccountType().equals(AccountType.ADMINISTRATOR);

            if (ownsShipment || hasAdminRole) {
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
        Account currentUser = accountService.getAccountFromJwt(principal);
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();

        if (currentUser.getAccountType().equals(AccountType.REGISTERED_USER)) {
            shipmentsByStatus = shipmentRepository.findAllBySenderAndShipmentStatus(currentUser, shipmentStatus);
        } else if (currentUser.getAccountType().equals(AccountType.ADMINISTRATOR)) {
            shipmentsByStatus = shipmentRepository.findAllByShipmentStatus(shipmentStatus);
        }
        commonResponse.data = shipmentsByStatus;
        commonResponse.message = "All shipments (completed or cancelled)";
        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> getShipmentsByCustomer(HttpServletRequest request, String keyCloakUserId) {
        Account customer;
        List<Shipment> shipmentsByCustomer;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!accountRepository.existsAccountByKeyCloakUserId(keyCloakUserId)) {
            commonResponse.data = null;
            commonResponse.message = "Customer and shipments not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            Optional<Account> accountRepos = accountRepository.getAccountByKeyCloakUserId(keyCloakUserId);
            customer = accountRepos.get();
            shipmentsByCustomer = shipmentRepository.findAllBySender(customer);
            commonResponse.data = shipmentsByCustomer;
            commonResponse.message = "Found shipments of customer with id: " + keyCloakUserId;
            resp = HttpStatus.OK;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> addShipment(HttpServletRequest request, Shipment shipment, Jwt principal) {
        Account currentUser = accountService.getAccountFromJwt(principal);
        Command cmd = new Command(request);
        HttpStatus resp;
        CommonResponse commonResponse = new CommonResponse();

        shipment.setShipmentStatus(ShipmentStatus.CREATED);
        shipment.setSender(currentUser);

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
//        String userId = principal.getClaimAsString("sub");
//        String userRole = principal.getClaimAsStringList("roles").get(0);
        Account currentUser = accountService.getAccountFromJwt(principal);
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

            boolean ownsShipment = shipment.getSender().getKeyCloakUserId().equals(currentUser.getKeyCloakUserId());
            boolean hasAdminRole = currentUser.getAccountType().equals(AccountType.ADMINISTRATOR);

            if (ownsShipment || hasAdminRole) {
                // Any non-Administrator users may only cancel a shipment
                if (currentUser.getAccountType().equals(AccountType.REGISTERED_USER)) {
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
}
