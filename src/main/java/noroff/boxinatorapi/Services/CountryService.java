package noroff.boxinatorapi.Services;

import noroff.boxinatorapi.Models.CommonResponse;
import noroff.boxinatorapi.Models.Country;
import noroff.boxinatorapi.Repositories.CountryRepository;
import noroff.boxinatorapi.Utilities.Command;
import noroff.boxinatorapi.Utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public ResponseEntity<CommonResponse> getAllCountries(HttpServletRequest request) {
        Command cmd = new Command(request);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.data = countryRepository.findAll();
        commonResponse.message = "All countries";

        HttpStatus resp = HttpStatus.OK;

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> addCountry(HttpServletRequest request, Country country) {
        Command cmd = new Command(request);
        HttpStatus resp;

        CommonResponse commonResponse = new CommonResponse();

        if (countryRepository.existsCountryByName(country.getName())) {
            commonResponse.data = null;
            commonResponse.message = "The country " + country.getName() + " has already been added";
            resp = HttpStatus.BAD_REQUEST;
        } else {
            country = countryRepository.save(country);
            commonResponse.data = country;
            commonResponse.message = "New country added, with id: " + country.getId();
            resp = HttpStatus.CREATED;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }

    public ResponseEntity<CommonResponse> updateCountry(HttpServletRequest request, Long id, Country updatedCountry) {
        Country country;
        Command cmd = new Command(request);
        CommonResponse commonResponse = new CommonResponse();
        HttpStatus resp;

        if (countryRepository.existsById(id)) {
            Optional<Country> countryRepos = countryRepository.findById(id);
            country = countryRepos.get();

            if (updatedCountry.getMultiplier() != null) {
                country.setMultiplier(updatedCountry.getMultiplier());
            }

            country = countryRepository.save(country);

            commonResponse.data = country;
            commonResponse.message = "Updated country with id: " + country.getId();
            resp = HttpStatus.OK;
        } else {
            commonResponse.data = null;
            commonResponse.message = "Country with id " + id + " not found";
            resp = HttpStatus.NOT_FOUND;
        }

        cmd.setResult(resp);
        Logger.getInstance().logCommand(cmd);
        return new ResponseEntity<>(commonResponse, resp);
    }
}
