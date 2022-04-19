package noroff.boxinatorapi.Repositories;

import noroff.boxinatorapi.Models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account>  findAccountByEmail(String email);
    Optional<Account> findAccountByKeycloakSubjectId(String keycloakSubjectId);
    boolean existsAccountByEmail(String email);
    boolean existsAccountByKeycloakSubjectId(String keyCloakSubjectId);
}
