package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.service.CsvReaderService;
import com.mycompany.myapp.service.JsonValidatorService;
import com.networknt.schema.ValidationMessage;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CsvController {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private JsonValidatorService jsonValidatorService;

    private final Logger log = LoggerFactory.getLogger(CsvController.class);

    @GetMapping("/convert")
    public ResponseEntity<List<Patient>> convertCSV() throws FileNotFoundException {
        List<Patient> dataList = new ArrayList<>();
        try {
            //            dataList = csvReaderService.readCsv("C:/Users/Raven/Downloads/RandomData.csv", Patient.class);
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
            return ResponseEntity.internalServerError().body(dataList);
        }

        return ResponseEntity.ok().body(dataList);
    }
}
