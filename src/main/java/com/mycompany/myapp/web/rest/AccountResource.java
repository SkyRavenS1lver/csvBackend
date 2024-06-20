package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mycompany.myapp.domain.Patient;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private static class AccountResourceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private AccountResourceException(String message) {
            super(message);
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @param principal the current user; resolves to {@code null} if not authenticated.
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserVM getAccount(Principal principal) {
        if (principal instanceof AbstractAuthenticationToken) {
            return getUserFromAuthentication((AbstractAuthenticationToken) principal);
        } else {
            throw new AccountResourceException("User could not be found");
        }
    }

    private static class UserVM {

        private String login;
        private Set<String> authorities;

        @JsonCreator
        UserVM(String login, Set<String> authorities) {
            this.login = login;
            this.authorities = authorities;
        }

        public boolean isActivated() {
            return true;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getLogin() {
            return login;
        }
    }

    //    @GetMapping("/convert")
    //    public List<List<String>> convertCSV() throws FileNotFoundException {
    //        List<List<String>> records = new ArrayList<List<String>>();
    //        try (CSVReader csvReader = new CSVReader(new FileReader("C:/Users/Raven/Downloads/patient_demographic.csv"));) {
    //            String[] values = null;
    //
    //            while ((values = csvReader.readNext()) != null) {
    //                records.add(Arrays.asList(values));
    //            }
    //
    //        } catch (Exception e) {
    //            log.info(e.getMessage());
    //            e.printStackTrace();
    //        }
    //        return records;
    //    }
    @GetMapping("/convert")
    public List<Patient> convertCSV() throws FileNotFoundException {
        List<Patient> records = new ArrayList();
        CSVReader reader = null;
        InputStream is = null;
        try {
            File initialFile = new File("C:/Users/Raven/Downloads/patient_demographic.csv");
            is = new FileInputStream(initialFile);
            reader = new CSVReader(new InputStreamReader(is));
            ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
            strat.setType(Patient.class);
            String[] columns = new String[] {
                "firstName",
                "lastName",
                "gender",
                "dob",
                "address",
                "suburb",
                "state",
                "postcode",
                "phone",
                "email",
            };
            strat.setColumnMapping(columns);
            CsvToBean csv = new CsvToBean();
            csv.setCsvReader(reader);
            csv.setMappingStrategy(strat);
            records = csv.parse();
            is.close();
            reader.close();
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        records.remove(0);
        return records;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    private UserVM getUserFromAuthentication(AbstractAuthenticationToken authToken) {
        if (!(authToken instanceof JwtAuthenticationToken)) {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }

        return new UserVM(
            authToken.getName(),
            authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
        );
    }
}
