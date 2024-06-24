package com.mycompany.myapp.service;

import static com.mycompany.myapp.constant.Constant.CSV_EXTENSION;
import static com.mycompany.myapp.constant.Constant.CSV_MIME_TYPE;

import com.mycompany.myapp.constant.Constant;
import org.springframework.web.multipart.MultipartFile;

public interface CSVFileValidator {
    default boolean isCsvFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        return contentType != null && contentType.equals(CSV_MIME_TYPE) && fileName != null && fileName.endsWith(CSV_EXTENSION);
    }
}
