package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.Account;
import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Repositories.AccountRepository;
import noroff.boxinatorapi.Utilities.Command;
import noroff.boxinatorapi.Utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountByJwt(Jwt principal) {
        // TODO: Implement account type
        Account account = new Account();
        String email = principal.getClaimAsString("email");
        String keyCloakSubjectId = principal.getClaimAsString("sub");
        String firstName = principal.getClaimAsString("given_name");
        String lastName = principal.getClaimAsString("family_name");
        String dateOfBirth = principal.getClaimAsString("dob");
        String countryOfResidence = principal.getClaimAsString("country_of_residence");
        String postalCode = principal.getClaimAsString("postal_code");
        String contactNumber = principal.getClaimAsString("contact_number");

        if (accountRepository.existsAccountByEmail(email)) {
            Optional<Account> accountRepos = accountRepository.findAccountByEmail(email);
            account = accountRepos.get();
        } else {
            // Account does not yet exist: set attributes of the new account
            account.setEmail(email);
            account.setKeycloakSubjectId(keyCloakSubjectId);
            account.setFirstName(firstName);
            account.setLastName(lastName);

            // Optional account attributes
            if (dateOfBirth != null) {
                account.setDateOfBirth(dateOfBirth);
            }
            if (countryOfResidence != null) {
                account.setCountryOfResidence(countryOfResidence);
            }
            if (postalCode != null) {
                account.setPostalCode(postalCode);
            }
            if (contactNumber != null) {
                account.setContactNumber(contactNumber);
            }

            account = accountRepository.save(account);
        }

        return account;
    }

    public ResponseEntity<CommonResponse> getAccountByKeycloakSubjectId(HttpServletRequest request, String keyCloakSubjectId) {
        Account account;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (accountRepository.existsAccountByKeycloakSubjectId(keyCloakSubjectId)) {
            Optional<Account> accountRepos = accountRepository.findAccountByKeycloakSubjectId(keyCloakSubjectId);
            account = accountRepos.get();
            commonResponse.data = account;
            commonResponse.message = "Found account with id: " + keyCloakSubjectId;
            resp = HttpStatus.OK;
        } else {
            commonResponse.data = null;
            commonResponse.message = "Account not found";
            resp = HttpStatus.NOT_FOUND;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }
}
