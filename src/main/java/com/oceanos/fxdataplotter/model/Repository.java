package com.oceanos.fxdataplotter.model;

import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class Repository {
    ObservableList<DataSourceViewModel> dataSources = FXCollections.observableArrayList();

    public void addDataSourceViewModel(DataSourceViewModel dataSource){
        dataSources.add(dataSource);
    }

    public ObservableList<DataSourceViewModel> getDataSources() {
        return dataSources;
    }
}
