package com.oceanos.fxdataplotter.data_adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testData.TestDataClass;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @autor slonikmak on 12.08.2019.
 */
class JSONAdapterTest {
    static JSONAdapter jsonAdapter;
    static TestDataClass dataClass;
    static String dataClassJsonString;
    static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() throws JsonProcessingException, DataParseException {
        jsonAdapter = new JSONAdapter();
        dataClass = new TestDataClass(3.,4.);
        objectMapper = new ObjectMapper();
        dataClassJsonString = objectMapper.writeValueAsString(dataClass);
        jsonAdapter.init(dataClassJsonString);
    }

    @Test
    void getFieldsTest() {
        assertEquals("data1", jsonAdapter.getFields().get(0).getName());
        assertEquals("data2", jsonAdapter.getFields().get(1).getName());
    }

    @Test
    void getValuesTest() throws DataParseException {
        List<DataField> fields = jsonAdapter.getFields();
        Map<DataField, Double> vals = jsonAdapter.getValues(dataClassJsonString);
        assertEquals(3., vals.get(fields.get(0)));
        assertEquals(4., vals.get(fields.get(1)));

    }
}