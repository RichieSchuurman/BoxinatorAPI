package noroff.boxinatorapi.Repositories;

import noroff.boxinatorapi.Models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsCountryByName(String name);
    Country getCountryByName(String name);
}
