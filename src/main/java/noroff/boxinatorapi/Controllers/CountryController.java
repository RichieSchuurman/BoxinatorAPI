package noroff.boxinatorapi.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Models.Country;
import noroff.boxinatorapi.Services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/settings/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Operation(summary = "Get all countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all countries",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Country.class))})
    })
    @GetMapping
    public ResponseEntity<CommonResponse> getAllCountries(HttpServletRequest request) {
        return countryService.getAllCountries(request);
    }

    @Operation(summary = "Add a new country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added new country",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "400", description = "Country has already been added",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class))})
    })
    @PostMapping
    public ResponseEntity<CommonResponse> addCountry(HttpServletRequest request, @RequestBody Country country) {
        return countryService.addCountry(request, country);
    }

    @Operation(summary = "Update a specific country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the selected country",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Country.class))}),
            @ApiResponse(responseCode = "404", description = "Country not found",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse> updateCountry(HttpServletRequest request,
                                                        @Parameter(description = "id of the country to be updated") @PathVariable Long id,
                                                        @RequestBody Country updatedCountry) {
        return countryService.updateCountry(request, id, updatedCountry);
    }
}
