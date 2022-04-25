package noroff.boxinatorapi.Repositories;

import noroff.boxinatorapi.Models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> getAccountByKeyCloakUserId(String keyCloakUserId);
    boolean existsAccountByKeyCloakUserId(String keyCloakUserId);
}
