package com.oceanos.fxdataplotter.data_adapters;


import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;

import java.util.List;
import java.util.Map;

/**
 * @autor slonikmak on 19.05.2019.
 */
public interface DataAdapter {
    void init(String initData) throws DataParseException;
    List<DataField> getFields();
    Map<DataField,Double> getValues(String dataString) throws DataParseException;
}
