package noroff.boxinatorapi.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import noroff.boxinatorapi.Models.Account;
import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@SecurityRequirement(name = "keycloak_implicit")
@RequestMapping("api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Get the account information of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found account information of specific user",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "404", description = "Account information not found",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class))})
    })
    @GetMapping("/{keycloakSubjectId}")
    public ResponseEntity<CommonResponse> getAccountByKeycloakSubjectId(
            HttpServletRequest request,
            @Parameter(description = "Keycloak subject id of the account to be fetched") @PathVariable String keycloakSubjectId) {
        return accountService.getAccountByKeycloakSubjectId(request, keycloakSubjectId);
    }
}
