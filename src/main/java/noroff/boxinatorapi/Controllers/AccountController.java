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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@SecurityRequirement(name = "keycloak_implicit")
@RequestMapping("api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Get the account information of a specific user (only accessible by registered users and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found account information of specific user",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "404", description = "Account information not found",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse> getAccountById(
            HttpServletRequest request, @Parameter(description = "id of the account to be fetched") @PathVariable String id) {
        return accountService.getAccountById(request, id);
    }

    @Operation(summary = "Add a new account (only accessible by administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added the new account",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Account already exists",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Only administrators can add a new account")
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<CommonResponse> addAccount(HttpServletRequest request, @RequestBody Account account) {
        return accountService.addAccount(request, account);
    }

    @Operation(summary = "Update a specific account (only accessible by the registered user who owns this account and administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the selected account",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "403", description = "Updating someone else's account is not allowed",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('REGISTERED_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse> updateAccount(
            HttpServletRequest request,
            @Parameter(description = "id of the account to be updated") @PathVariable  String id,
            @RequestBody Account updatedAccount,
            @AuthenticationPrincipal Jwt principal) {
        return accountService.updateAccount(request, id, updatedAccount, principal);
    }

    @Operation(summary = "Delete an account (only accessible by administrators)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted the selected account"),
            @ApiResponse(responseCode = "403", description = "Only administrators can delete an account"),
            @ApiResponse(responseCode = "404", description = "Account to be deleted not found",
                        content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class))})
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse> deleteAccount(
            HttpServletRequest request,
            @Parameter(description = "id of the account to be deleted") @PathVariable String id) {
        return accountService.deleteAccount(request, id);
    }
}
