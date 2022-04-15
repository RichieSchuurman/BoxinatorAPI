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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@SecurityRequirement(name = "keycloak_implicit")
@RequestMapping("api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Operation(summary = "Get all shipments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping
    public ResponseEntity<CommonResponse> getAllShipments(HttpServletRequest request) {
        return shipmentService.getAllShipments(request);
    }

    @Operation(summary = "Get all completed shipments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all completed shipments",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Shipment.class))})
    })
    @GetMapping
    public ResponseEntity<CommonResponse> getCompletedShipments(HttpServletRequest request,
                                                                @RequestParam("shipmentStatus") ShipmentStatus shipmentStatus) {


        return shipmentService.getCompletedShipments(request, shipmentStatus);
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
                                                        @Parameter(description = "id of the shipment to be updated") @PathVariable Long id) {
        return shipmentService.updateShipment(request, id);
    }


}
