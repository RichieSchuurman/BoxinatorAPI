package noroff.boxinatorapi.Controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Models.Country;
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

    @Operation(summary = "Get all shipments (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping
    public ResponseEntity<CommonResponse> getAllShipments(HttpServletRequest request, @AuthenticationPrincipal Jwt principal) {
        return shipmentService.getAllShipments(request, principal);
    }

    //TODO add specific enum type to API call
    @Operation(summary = "Get all completed shipments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all completed shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping("/complete")
    public ResponseEntity<CommonResponse> getCompletedShipments(HttpServletRequest request,
                                                                @RequestParam("shipmentStatus") ShipmentStatus shipmentStatus) {


        return shipmentService.getCompletedShipments(request, shipmentStatus);
    }

    //TODO add specific enum type to API call?
    @Operation(summary = "Get all cancelled shipments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all cancelled shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping("/cancelled")
    public ResponseEntity<CommonResponse> getCancelledShipments(HttpServletRequest request,
                                                                @RequestParam("shipmentStatus") ShipmentStatus shipmentStatus) {


        return shipmentService.getCanceledShipments(request, shipmentStatus);
    }

    @Operation(summary = "Add a new shipment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added new shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "400", description = "Shipment has already been added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PostMapping
    public ResponseEntity<CommonResponse> addShipment(HttpServletRequest request, @RequestBody Shipment shipment) {
        return shipmentService.addShipment(request, shipment);
    }

    @Operation(summary = "Update a specific shipment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the selected shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse> updateShipment(HttpServletRequest request,
                                                        @Parameter(description = "id of the shipment to be updated") @PathVariable Long id,
                                                         @RequestBody Shipment updatedShipment) {
        return shipmentService.updateShipment(request, id, updatedShipment);
    }

    @Operation(summary = "Get shipment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found shipment with id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse> getShipmentById(HttpServletRequest request,
                                                          @Parameter(description = "id of shipment to be searched") @PathVariable Long id) {
        return shipmentService.getShipmentById(request, id);
    }

    //TODO
    //GET /shipments/customer/:customer_id

    @Operation(summary = "Delete a specific shipment (only accessible by administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete the selected shipment",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "404", description = "Shipment not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteShipment(HttpServletRequest request,
                                                         @Parameter(description = "id of the shipment to be deleted") @PathVariable Long id) {
        return shipmentService.deleteShipment(request, id);
    }
}
