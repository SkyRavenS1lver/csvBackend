package com.mycompany.myapp.service;

import java.nio.file.Files;
import java.nio.file.Path;

public interface UniqueNameGenerator {
    default String generateUniqueFileName(Path uploadPath, String originalFileName) {
        String fileName = originalFileName;
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = originalFileName.substring(0, dotIndex);
            fileExtension = originalFileName.substring(dotIndex);
        }

        Path filePath = uploadPath.resolve(originalFileName);
        int counter = 1;
        while (Files.exists(filePath)) {
            String newFileName = fileName + "(" + counter + ")" + fileExtension;
            filePath = uploadPath.resolve(newFileName);
            counter++;
        }

        return filePath.getFileName().toString();
    }
}
