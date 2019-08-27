package com.oceanos.fxdataplotter.data_adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;

import java.io.IOException;
import java.util.*;

/**
 * @autor slonikmak on 15.05.2019.
 */
public class JSONAdapter implements DataAdapter{

    private List<DataField> fields;
    private ObjectMapper mapper;

    public JSONAdapter(){
        mapper = new ObjectMapper();
    }

    public void init(String initData) throws DataParseException {
        Map<String, String> fieldsMap = null;
        fields = new ArrayList<>();
        try {
            fieldsMap = mapper.readValue(initData, Map.class);
        } catch (IOException e) {
            throw new DataParseException(e.getMessage());
        }
        Set<String> keySet = fieldsMap.keySet();
        int i = 0;
        for (String key :
                keySet) {
            fields.add(new DataField(key, i));
            i++;
        }

    }

    public List<DataField> getFields() {
        return fields;
    }

    @Override
    public Map<DataField, Double> getValues(String dataString) throws DataParseException {
        Map<DataField, Double> valsMap = new HashMap<>();
        try {
            Map<String, Object> result = mapper.readValue(dataString, Map.class);
            fields.forEach(f->valsMap.put(f, (Double)result.get(f.getOldName())));

        } catch (IOException e) {
            throw new DataParseException(e.getMessage());
        }
        return valsMap;
    }

    @Override
    public void setFields(List<DataField> fields) {
        this.fields = fields;
    }
}
