package com.oceanos.fxdataplotter.view;

import com.oceanos.fxdataplotter.chartview.DataFieldViewModel;
import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import com.oceanos.fxdataplotter.utils.MyDoubleStringConverter;
import com.oceanos.fxdataplotter.viewmodel.DataSourceTableViewModel;
import de.saxsys.mvvmfx.JavaView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


/**
 * @autor slonikmak on 15.08.2019.
 */
public class DataSourceTableView extends AnchorPane implements JavaView<DataSourceTableViewModel> {

    private TableView<DataFieldViewModel> dataFieldTable;

    public DataSourceTableView(){

    }

    public void init(DataSourceViewModel dataSourceViewModel){

        dataFieldTable = new TableView<>();

        dataFieldTable.setEditable(true);

        /////INDEX
        TableColumn<DataFieldViewModel, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setPrefWidth(25);
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        ////NAME
        TableColumn<DataFieldViewModel, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setPrefWidth(70);
        nameColumn.setCellValueFactory(param -> {
            DataFieldViewModel cellData = param.getValue();
            SimpleStringProperty stringProp = new SimpleStringProperty(cellData.nameProperty().get());
            cellData.nameProperty().bindBidirectional(stringProp);
            //stringProp.addListener((observable, oldValue, newValue) -> cellData.setName(newValue));
            return stringProp;
        });
        nameColumn.setCellFactory(EditCell.forTableColumn());
        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<DataFieldViewModel, String> event) -> {
            TablePosition<DataFieldViewModel, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            int row = pos.getRow();
            DataFieldViewModel cellData = event.getTableView().getItems().get(row);
            cellData.setName(newName);
            dataFieldTable.refresh();
        });

        ///MIN
        TableColumn<DataFieldViewModel, Double> minColumn = new TableColumn<>("Мин");
        minColumn.setPrefWidth(40);
        minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
        minColumn.setCellFactory(EditCell.forTableColumn(new MyDoubleStringConverter()));
        minColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setMin(value);
            dataFieldTable.refresh();
        });


        //MAX
        TableColumn<DataFieldViewModel, Double> maxColumn = new TableColumn<>("Макс");
        maxColumn.setPrefWidth(40);
        maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
        maxColumn.setCellFactory(EditCell.forTableColumn(new MyDoubleStringConverter()));
        maxColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()).setMax(value);
            dataFieldTable.refresh();
        });


        //COLOR
        TableColumn<DataFieldViewModel, Color> colorColumn = new TableColumn<>("Цвет");
        colorColumn.setPrefWidth(90);
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(param -> {
            EditColorPickerCell<DataFieldViewModel> cell = new EditColorPickerCell<>(colorColumn);
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        ///ENABLE
        TableColumn<DataFieldViewModel, Boolean> enabledColumn = new TableColumn<>("Добавить");
        enabledColumn.setPrefWidth(35);
        enabledColumn.setCellValueFactory(param -> {
            DataFieldViewModel cellData = param.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(cellData.enabledProperty().get());
            // Note: singleCol.setOnEditCommit(): Not work for
            // CheckBoxTableCell.
            // When "Single?" column change.
            booleanProp.addListener((observable, oldValue, newValue) -> cellData.setEnabled(newValue));
            return booleanProp;
        });


        enabledColumn.setCellFactory(p -> {
            CheckBoxTableCell<DataFieldViewModel, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        dataFieldTable.getColumns().addAll(indexColumn, nameColumn, minColumn, maxColumn, colorColumn, enabledColumn);

        dataFieldTable.setItems(dataSourceViewModel.getDataFieldViewModels());

        getChildren().add(dataFieldTable);

    }


}
