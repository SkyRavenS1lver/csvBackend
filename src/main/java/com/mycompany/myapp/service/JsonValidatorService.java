package com.mycompany.myapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.InputStream;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class JsonValidatorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Set<ValidationMessage> validateJson(String jsonData, String schemaPath) throws Exception {
        InputStream schemaStream = getClass().getResourceAsStream("/" + schemaPath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema file not found: " + schemaPath);
        }
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        JsonSchema schema = schemaFactory.getSchema(schemaStream);
        JsonNode jsonNode = objectMapper.readTree(jsonData);
        return schema.validate(jsonNode);
    }
}
