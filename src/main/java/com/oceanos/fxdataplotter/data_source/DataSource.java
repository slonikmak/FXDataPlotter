package com.oceanos.fxdataplotter.data_source;

import com.oceanos.fxdataplotter.connections.Connection;
import com.oceanos.fxdataplotter.data_adapters.DataAdapter;
import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Map;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class DataSource {

    private Connection connection;
    private DataAdapter dataAdapter;
    private String name;
    private StringProperty sampleData = new SimpleStringProperty();


    public DataSource(Connection connection, DataAdapter dataAdapter) {
        this.connection = connection;
        this.dataAdapter = dataAdapter;
        init();
    }

    private void init(){
        connection.getSampleData().thenAccept(data->{
            try {
                dataAdapter.init(data);
                sampleData.setValue(data);
            } catch (DataParseException e) {
                e.printStackTrace();
            }
        });

        connection.setOnReceived(s->{
            sampleDataProperty().setValue(s);
            try {
                Map<DataField, Double> values = getValues(s);
                getFields().forEach(f->f.setValue(values.get(f)));
            } catch (DataParseException e) {
                e.printStackTrace();
            }
        });

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSampleData() {
        return sampleData.get();
    }

    public StringProperty sampleDataProperty() {
        return sampleData;
    }

    public List<DataField> getFields() {
        return dataAdapter.getFields();
    }

    public Map<DataField, Double> getValues(String data) throws DataParseException {
        return dataAdapter.getValues(data);
    }

    public void stop(){
        connection.close();
    }
}
