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

    public ResponseEntity<CommonResponse> getAccountById(HttpServletRequest request, String id) {
        Account account;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!accountRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Account not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            Optional<Account> accountRepos = accountRepository.findById(id);
            account = accountRepos.get();
            commonResponse.data = account;
            commonResponse.message = "Found account with id: " + account.getId();
            resp = HttpStatus.OK;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> addAccount(HttpServletRequest request, Account account) {
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!accountRepository.existsById(account.getId())) {
            account.setAccountType(AccountType.REGISTERED_USER);
            account = accountRepository.save(account);
            commonResponse.data = account;
            commonResponse.message = "Added new account with id " + account.getId();
            resp = HttpStatus.CREATED;
        } else {
            commonResponse.data = null;
            commonResponse.message = "Account already exists";
            resp = HttpStatus.BAD_REQUEST;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> updateAccount(
            HttpServletRequest request,
            String id,
            Account updatedAccount,
            Jwt principal) {
        Account account;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;
        String userRole = principal.getClaimAsStringList("roles").get(0);

        if (!AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR) && !principal.getClaimAsString("sub").equals(id)) {
            commonResponse.data = null;
            commonResponse.message = "You are only allowed to update your own account";
            resp = HttpStatus.FORBIDDEN;
        } else {
            Optional<Account> accountRepos = accountRepository.findById(id);
            account = accountRepos.get();

            if (updatedAccount.getFirstName() != null) {
                account.setFirstName(updatedAccount.getFirstName());
            }

            if (updatedAccount.getLastName() != null) {
                account.setLastName(updatedAccount.getLastName());
            }

            if (updatedAccount.getEmail() != null) {
                account.setEmail(updatedAccount.getEmail());
            }

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

            if (AccountType.valueOf(userRole).equals(AccountType.ADMINISTRATOR)) {
                if (updatedAccount.getAccountType() != null) {
                    account.setAccountType(updatedAccount.getAccountType());
                }
            }

            account = accountRepository.save(account);
            commonResponse.data = account;
            commonResponse.message = "Updated account with id " + id;
            resp = HttpStatus.OK;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> deleteAccount(HttpServletRequest request, String id) {
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (!accountRepository.existsById(id)) {
            commonResponse.data = null;
            commonResponse.message = "Account not found";
            resp = HttpStatus.NOT_FOUND;
        } else {
            accountRepository.deleteById(id);
            resp = HttpStatus.NO_CONTENT;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }
}
