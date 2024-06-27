package com.mycompany.myapp.service;

import com.mycompany.myapp.constant.Constant;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class CsvFileService {

    public List<String> getCsvFileNames() throws IOException {
        Path path = Paths.get(Constant.FILE_DIRECTORY);
        try (Stream<Path> paths = Files.walk(path, 1)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(Constant.CSV_EXTENSION))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        }
    }

    public boolean deleteCsvFile(String fileName) throws IOException {
        Path path = Paths.get(Constant.FILE_DIRECTORY).resolve(fileName);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return Files.deleteIfExists(path);
        } else {
            throw new NoSuchFileException("File not found: " + fileName);
        }
    }
}
