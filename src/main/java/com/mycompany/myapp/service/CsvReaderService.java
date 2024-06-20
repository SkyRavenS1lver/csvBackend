package com.mycompany.myapp.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CsvReaderService {

    public <T> List<T> readCsv(String filePath, Class<T> type) throws Exception {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withType(type).withIgnoreLeadingWhiteSpace(true).build();
            return csvToBean.parse();
        }
    }
}
