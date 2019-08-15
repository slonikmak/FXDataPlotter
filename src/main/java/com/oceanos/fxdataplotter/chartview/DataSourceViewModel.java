package com.oceanos.fxdataplotter.chartview;

import com.oceanos.fxdataplotter.data_source.DataSource;
import com.oceanos.fxdataplotter.exceptions.DataParseException;
import com.oceanos.fxdataplotter.model.DataField;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Map;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class DataSourceViewModel {
    private DataSource dataSource;
    private ObservableList<DataFieldViewModel> dataFieldViewModels;

    public DataSourceViewModel(){

    }

    public DataSourceViewModel(DataSource dataSource, ObservableList<DataFieldViewModel> dataFieldViewModels) {
        this.dataSource = dataSource;
        this.dataFieldViewModels = dataFieldViewModels;
        dataSource.sampleDataProperty().addListener((o,oV,nV)->{
            try {
                Map<DataField, Double> values =  dataSource.getValues(nV);
                dataFieldViewModels.forEach(dataFieldViewModel -> {
                    Double val = values.get(dataFieldViewModel.getDataField());
                    dataFieldViewModel.setCurrentValue(val);
                });

            } catch (DataParseException e) {
                e.printStackTrace();
            }
        });
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ObservableList<DataFieldViewModel> getDataFieldViewModels() {
        return FXCollections.unmodifiableObservableList(dataFieldViewModels);
    }

    public StringProperty getLastDataProperty(){
        return dataSource.sampleDataProperty();
    }

    @Override
    public String toString() {
        return dataSource.getName();
    }
}
