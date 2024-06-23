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

    private final ResourceLoader resourceLoader;

    public CsvFileService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> getCsvFileNames() throws IOException {
        Path path = Paths.get(Constant.FILE_DIRECTORY);
        try (Stream<Path> paths = Files.walk(path, 1)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        }
    }
}
