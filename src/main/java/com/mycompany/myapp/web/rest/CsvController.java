package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.constant.Constant;
import com.mycompany.myapp.domain.Patient;
import com.mycompany.myapp.service.*;
import com.networknt.schema.ValidationMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
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
@RequestMapping("/api/csv")
public class CsvController implements DateValidatorUsingDateTimeFormatter, UniqueNameGenerator, CSVFileValidator {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private JsonValidatorService jsonValidatorService;

    @Autowired
    private CsvFileService csvFileService;

    private final Logger log = LoggerFactory.getLogger(CsvController.class);

    @GetMapping("/all-files")
    public ResponseEntity<List<String>> getCsvFiles() {
        try {
            List<String> csvFileNames = csvFileService.getCsvFileNames();
            return ResponseEntity.ok(csvFileNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }
        try {
            Path uploadPath = Paths.get(Constant.FILE_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            if (!isCsvFile(file)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only CSV files are allowed.");
            }
            //            // Check if the file already exists
            //            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            //            if (Files.exists(filePath)) {
            //                return ResponseEntity.status(HttpStatus.CONFLICT).body("File already exists");
            //            }
            //            Files.copy(file.getInputStream(), filePath);

            //            // Save the file to the upload directory, overwriting if it exists
            //            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            //            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //            // Generate a unique file name if file already exists
            //            String fileName = generateUniqueFileName(uploadPath, Objects.requireNonNull(file.getOriginalFilename()));
            //            Path filePath = uploadPath.resolve(fileName);
            //            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save file using date
            String dates = (new SimpleDateFormat("dd-MMM-yy HH-mm-ss ")).format(new Date());
            Path filePath = uploadPath.resolve(dates + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded and saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Patient>> convertCSV(@RequestParam String name) throws FileNotFoundException {
        List<Patient> dataList = new ArrayList<>();
        try {
            dataList = csvReaderService.readCsv(Constant.FILE_DIRECTORY + "/" + name, Patient.class);
            for (Patient data : dataList) {
                String tempDate = data.getDob();
                isValidAndConvert(data);
                String jsonData = new ObjectMapper().writeValueAsString(data);
                Set<ValidationMessage> errors = jsonValidatorService.validateJson(jsonData, Constant.SCHEMA_PATH_PATIENT);
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

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteCsvFile(@RequestParam String name) {
        try {
            boolean deleted = csvFileService.deleteCsvFile(name);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
