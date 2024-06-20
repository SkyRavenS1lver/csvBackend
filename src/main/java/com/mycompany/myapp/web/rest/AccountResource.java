package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.service.CsvReaderService;
import com.mycompany.myapp.service.JsonValidatorService;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountResource {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private JsonValidatorService jsonValidatorService;

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

    @GetMapping("/convert")
    public List<Patient> convertCSV() throws FileNotFoundException {
        List<Patient> dataList = new ArrayList<>();
        try {
            // Assuming CSV file contains data that maps to a MyData class
            dataList = csvReaderService.readCsv("C:/Users/Raven/Downloads/patient_demographic.csv", Patient.class);
            for (Patient data : dataList) {
                String tempDate = data.getDob();
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                    Date date = formatter.parse(tempDate);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    data.setDob(dateFormat.format(date));
                } catch (Exception ignored) {}
                String jsonData = new ObjectMapper().writeValueAsString(data);
                Set<ValidationMessage> errors = jsonValidatorService.validateJson(jsonData, "model/patient.schema.json");
                if (!errors.isEmpty()) {
                    data.setErrors(errors);
                }
                data.setDob(tempDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error occurred: " + e.getMessage());
        }
        System.out.println(dataList);
        return dataList;
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
