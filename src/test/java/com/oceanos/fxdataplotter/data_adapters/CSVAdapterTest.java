package com.oceanos.fxdataplotter.data_adapters;

import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @autor slonikmak on 12.08.2019.
 */
class CSVAdapterTest {
    static String delimiter = ";";
    static CSVAdapter csvAdapter;
    static String testDataString = "1;2;3";

    @BeforeAll
    static void setup() throws DataParseException {
        csvAdapter = new CSVAdapter();
        csvAdapter.setDelimiter(delimiter);
        csvAdapter.init(testDataString);
    }

    @Test
    void getFieldsTest() {
        assertEquals("0", csvAdapter.getFields().get(0).getName());
        assertEquals("1", csvAdapter.getFields().get(1).getName());
    }

    @Test
    void getValuesTest() throws DataParseException {
        List<DataField> fields = csvAdapter.getFields();
        Map<DataField, Double> vals = csvAdapter.getValues(testDataString);
        assertEquals(1., vals.get(fields.get(0)));
        assertEquals(2., vals.get(fields.get(1)));

    }


}