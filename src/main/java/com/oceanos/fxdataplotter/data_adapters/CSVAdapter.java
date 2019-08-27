package com.oceanos.fxdataplotter.data_adapters;


import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor slonikmak on 24.05.2019.
 */
public class CSVAdapter implements DataAdapter{

    private List<DataField> fields;
    private String delimiter;

    @Override
    public void init(String initData) throws DataParseException {
        String[] values = initData.split(delimiter);
        fields = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            fields.add(new DataField(String.valueOf(i),i));
        }
    }

    @Override
    public List<DataField> getFields() {
        return fields;
    }

    @Override
    public Map<DataField, Double> getValues(String dataString) throws DataParseException {

        Map<DataField, Double> valsMap = new HashMap<>();
        String[] vals = dataString.split(delimiter);
        for (int i = 0; i < vals.length; i++) {
            valsMap.put(fields.get(i), Double.parseDouble(vals[i]));
        }
        return valsMap;
    }

    @Override
    public void setFields(List<DataField> fields) {

    }

    public void setDelimiter(String delemiter){
        this.delimiter = delemiter;
    }
}
