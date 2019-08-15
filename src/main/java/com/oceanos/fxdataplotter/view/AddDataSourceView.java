package com.oceanos.fxdataplotter.view;

import com.google.inject.Inject;
import com.oceanos.fxdataplotter.connections.Connection;
import com.oceanos.fxdataplotter.connections.ZeroMQConnection;
import com.oceanos.fxdataplotter.data_adapters.CSVAdapter;
import com.oceanos.fxdataplotter.data_adapters.DataAdapter;
import com.oceanos.fxdataplotter.data_adapters.JSONAdapter;
import com.oceanos.fxdataplotter.data_source.DataSource;
import com.oceanos.fxdataplotter.model.Repository;
import com.oceanos.fxdataplotter.utils.MyDoubleStringConverter;
import com.oceanos.fxdataplotter.viewmodel.AddDataSourceViewModel;
import com.oceanos.fxdataplotter.chartview.DataFieldViewModel;
import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import de.saxsys.mvvmfx.FxmlView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class AddDataSourceView implements FxmlView<AddDataSourceViewModel> {

    private DataSource dataSource;
    private Connection connection;
    private DataAdapter dataAdapter;
    private ObservableList<DataFieldViewModel> dataFieldViewModels = FXCollections.observableArrayList();

    @Inject
    Repository repository;
    Stage stage;


    @FXML
    private RadioButton zeroMQbtn;

    @FXML
    private ToggleGroup connectionGroup;

    @FXML
    private RadioButton tcpBtn;

    @FXML
    private RadioButton serialBtn;

    @FXML
    private TextField addressField;

    @FXML
    private TextField topicField;

    @FXML
    private TextArea sampleStringField;

    @FXML
    private VBox adapterPane;

    @FXML
    private RadioButton jsonBtn;

    @FXML
    private ToggleGroup adapterGroup;

    @FXML
    private RadioButton csvBtn;

    @FXML
    private TextField delimiterField;

    @FXML
    private TableView<DataFieldViewModel> dataFieldTable;

    @FXML
    private TextField nameField;

    @FXML
    void add(ActionEvent event) {
        repository.addDataSourceViewModel(new DataSourceViewModel(dataSource, dataFieldViewModels));
        dataSource.setName(nameField.getText());
        stage.close();
    }

    @FXML
    void cancel(ActionEvent event) {
        stage.close();
    }

    @FXML
    void connect(ActionEvent event) {

        Optional<Connection> connectionOptional = Optional.empty();

        Class connectionClass = (Class) zeroMQbtn.getToggleGroup().getSelectedToggle().getUserData();

        if (connectionClass.equals(ZeroMQConnection.class)) {
            connectionOptional = Optional.of(new ZeroMQConnection(addressField.getText(), topicField.getText()));
        }
        connectionOptional.ifPresent(con -> {
            con.getSampleData().thenAccept(data -> sampleStringField.setText(data));
            connection = con;
        });


    }

    @FXML
    void showDataSource(ActionEvent event) {
        Optional<DataAdapter> dataAdapter = Optional.empty();
        Class dataAdapterClass = (Class) jsonBtn.getToggleGroup().getSelectedToggle().getUserData();


        if (dataAdapterClass.equals(JSONAdapter.class)) {
            dataAdapter = Optional.of(new JSONAdapter());
        } else if (dataAdapterClass.equals(CSVAdapter.class)) {
            CSVAdapter csvAdapter = new CSVAdapter();
            csvAdapter.setDelimiter(delimiterField.getText());
            dataAdapter = Optional.of(csvAdapter);
        }

        if (connection != null && dataAdapter.isPresent()) {
            dataSource = new DataSource(connection, dataAdapter.get());

            dataFieldViewModels.addAll(dataSource
                    .getFields()
                    .stream()
                    .map(f -> new DataFieldViewModel(f.getName(), f.getPosition(), f))
                    .collect(Collectors.toList()));

            System.out.println("show");
        }
    }

    public void initialize() {
        zeroMQbtn.setUserData(ZeroMQConnection.class);
        jsonBtn.setUserData(JSONAdapter.class);
        csvBtn.setUserData(CSVAdapter.class);

        initTable();
    }

    public void initTable() {

        dataFieldTable.setEditable(true);

        /////INDEX
        TableColumn<DataFieldViewModel, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setPrefWidth(38);
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        ////NAME
        TableColumn<DataFieldViewModel, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setPrefWidth(70);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<DataFieldViewModel>forTableColumn());
        nameColumn.setOnEditCommit((TableColumn.CellEditEvent<DataFieldViewModel, String> event) -> {
            TablePosition<DataFieldViewModel, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            int row = pos.getRow();
            DataFieldViewModel cellData = event.getTableView().getItems().get(row);
            cellData.setName(newName);
        });

        ///MIN
        TableColumn<DataFieldViewModel, Double> minColumn = new TableColumn<>("Мин");
        minColumn.setPrefWidth(60);
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
        maxColumn.setPrefWidth(60);
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
        enabledColumn.setPrefWidth(65);
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

        dataFieldTable.setItems(dataFieldViewModels);

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
