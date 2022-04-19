package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.Account;
import noroff.boxinatorapi.Models.AccountType;
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
        Account account = new Account();
        String email = principal.getClaimAsString("email");
        String keyCloakSubjectId = principal.getClaimAsString("sub");
        String firstName = principal.getClaimAsString("given_name");
        String lastName = principal.getClaimAsString("family_name");
        String userRole = principal.getClaimAsStringList("roles").get(0);

        if (accountRepository.existsAccountByEmail(email)) {
            Optional<Account> accountRepos = accountRepository.findAccountByEmail(email);
            account = accountRepos.get();
        } else {
            // Account does not yet exist: set attributes of the new account
            account.setEmail(email);
            account.setKeycloakSubjectId(keyCloakSubjectId);
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setAccountType(AccountType.valueOf(userRole));

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

    public ResponseEntity<CommonResponse> updateAccount(
            HttpServletRequest request,
            String keyCloakSubjectId,
            Account updatedAccount,
            Jwt principal) {
        Account account;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp = null;

        if (!principal.getClaimAsString("sub").equals(keyCloakSubjectId)) {
            commonResponse.data = null;
            commonResponse.message = "You are only allowed to update your own account";
            resp = HttpStatus.FORBIDDEN;
        } else if (accountRepository.existsAccountByKeycloakSubjectId(keyCloakSubjectId)) {
            Optional<Account> accountRepos = accountRepository.findAccountByKeycloakSubjectId(keyCloakSubjectId);
            account = accountRepos.get();

            if (updatedAccount.getDateOfBirth() != null) {
                account.setDateOfBirth(updatedAccount.getDateOfBirth());
            }
            if (updatedAccount.getContactNumber() != null) {
                account.setContactNumber(updatedAccount.getContactNumber());
            }
            if (updatedAccount.getCountryOfResidence() != null) {
                account.setCountryOfResidence(updatedAccount.getCountryOfResidence());
            }
            if (updatedAccount.getPostalCode() != null) {
                account.setPostalCode(updatedAccount.getPostalCode());
            }

            account = accountRepository.save(account);

            commonResponse.data = account;
            commonResponse.message = "Updated account with id: " + keyCloakSubjectId;
            resp = HttpStatus.OK;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }
}
