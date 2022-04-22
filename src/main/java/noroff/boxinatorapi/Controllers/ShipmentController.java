package noroff.boxinatorapi.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Models.Shipment;
import noroff.boxinatorapi.Models.ShipmentStatus;
import noroff.boxinatorapi.Services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@SecurityRequirement(name = "keycloak_implicit")
@RequestMapping("api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Operation(summary = "Get all current shipments (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all current shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping
    public ResponseEntity<CommonResponse> getAllCurrentShipments(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) {
        return shipmentService.getAllCurrentShipments(request, principal);
    }

    @Operation(summary = "Get the details of a specific shipment (only accessible by the registered user who owns this shipment and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the selected shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))}),
            @ApiResponse(responseCode = "403", description = "Users can only view their own shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse> getShipmentById(
            HttpServletRequest request,
            @Parameter(description = "id of the shipment to be fetched") @PathVariable Long id,
            @AuthenticationPrincipal Jwt principal) {
        return shipmentService.getShipmentById(request, id, principal);
    }

    @Operation(summary = "Get all completed shipments (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all completed shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping("/complete")
    public ResponseEntity<CommonResponse> getCompletedShipments(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) {
        return shipmentService.getShipmentsByStatus(request, ShipmentStatus.COMPLETED, principal);
    }

    @Operation(summary = "Get all cancelled shipments (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all cancelled shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping("/cancelled")
    public ResponseEntity<CommonResponse> getCancelledShipments(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) {
        return shipmentService.getShipmentsByStatus(request, ShipmentStatus.CANCELED, principal);
    }

    @Operation(summary = "Get all shipments a specific customer has made (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found shipments made by the selected customer",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))}),
            @ApiResponse(responseCode = "404", description = "Customer and shipments not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping("/customer/{id}")
    public ResponseEntity<CommonResponse> getShipmentsByCustomer(
            HttpServletRequest request,
            @Parameter(description = "id of the customer to be fetched") @PathVariable String id) {
        return shipmentService.getShipmentsByCustomer(request, id);
    }

    @Operation(summary = "Add a new shipment (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added the new shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))}),
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @PostMapping
    public ResponseEntity<CommonResponse> addShipment(
            HttpServletRequest request,
            @RequestBody Shipment shipment,
            @AuthenticationPrincipal Jwt principal) {
        return shipmentService.addShipment(request, shipment, principal);
    }

    @Operation(summary = "Update a shipment (only accessible by the registered user who owns this shipment and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the selected shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))}),
            @ApiResponse(responseCode = "403", description = "Users can only update their own shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse> updateShipment(
            HttpServletRequest request,
            @Parameter(description = "id of the shipment to be updated") @PathVariable Long id,
            @RequestBody Shipment updatedShipment,
            @AuthenticationPrincipal Jwt principal) {
        return shipmentService.updateShipment(request, id, updatedShipment, principal);
    }

    @Operation(summary = "Delete a specific shipment (only accessible by administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted the selected shipment"),
            @ApiResponse(responseCode = "403", description = "Only administrators can delete a shipment"),
            @ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteShipment(
            HttpServletRequest request,
            @Parameter(description = "id of the shipment to be deleted") @PathVariable Long id) {
        return shipmentService.deleteShipment(request, id);
    }
}
