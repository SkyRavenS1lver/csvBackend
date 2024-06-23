package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.constant.Constant;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.service.CsvFileService;
import com.mycompany.myapp.service.CsvReaderService;
import com.mycompany.myapp.service.JsonValidatorService;
import com.networknt.schema.ValidationMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class CsvController {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private JsonValidatorService jsonValidatorService;

    @Autowired
    private CsvFileService csvFileService;

    private final Logger log = LoggerFactory.getLogger(CsvController.class);

    @GetMapping("/convert/all-files")
    public ResponseEntity<List<String>> getCsvFiles() {
        try {
            List<String> csvFileNames = csvFileService.getCsvFileNames();
            return ResponseEntity.ok(csvFileNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }
        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(Constant.FILE_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Check if the file already exists
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            //            if (Files.exists(filePath)) {
            //                return ResponseEntity.status(HttpStatus.CONFLICT).body("File already exists");
            //            }
            //            Files.copy(file.getInputStream(), filePath);

            // Save the file to the upload directory, overwriting if it exists
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded and saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }
    }

    @GetMapping("/convert")
    public ResponseEntity<List<Patient>> convertCSV(@RequestParam String name) throws FileNotFoundException {
        List<Patient> dataList = new ArrayList<>();
        try {
            //            dataList = csvReaderService.readCsv("C:/Users/Raven/Downloads/RandomData.csv", Patient.class);
            dataList = csvReaderService.readCsv(Constant.FILE_DIRECTORY + "/" + name, Patient.class);
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
