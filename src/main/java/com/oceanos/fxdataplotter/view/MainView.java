package com.oceanos.fxdataplotter.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.oceanos.fxdataplotter.chartview.ChartView;
import com.oceanos.fxdataplotter.chartview.DataFieldViewModel;
import com.oceanos.fxdataplotter.connections.Connection;
import com.oceanos.fxdataplotter.connections.ZeroMQConnection;
import com.oceanos.fxdataplotter.data_adapters.DataAdapter;
import com.oceanos.fxdataplotter.data_adapters.JSONAdapter;
import com.oceanos.fxdataplotter.data_source.DataSource;
import com.oceanos.fxdataplotter.logger.DataLogger;
import com.oceanos.fxdataplotter.model.DataField;
import com.oceanos.fxdataplotter.preference.AppPreference;
import com.oceanos.fxdataplotter.viewmodel.AddDataSourceViewModel;
import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import com.oceanos.fxdataplotter.viewmodel.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class MainView implements FxmlView<MainViewModel> {




    @InjectViewModel
    MainViewModel viewModel;
    @Inject
    private Stage primaryStage;

    private DataLogger dataLogger;


    private AppPreference appPreference;

    private ChartView chartView;

    @FXML
    private TreeView<DataSourceViewModel> dataSetTree;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private TextField logFolderField;

    @FXML
    private TextField fileName;

    @FXML
    private ToggleButton startLogBtn;

    @FXML
    private ToggleButton stopLogBtn;

    @FXML
    private TextField delimiterField;

    @FXML
    private TextField logDescription;

    @FXML
    private AnchorPane dataFieldsPane;

    @FXML
    private TextField timeRangeField;

    @FXML
    private ColorPicker linePicker;

    @FXML
    private void addLine(){
        addLineNode();
    }


    @FXML
    private void startLogging(ActionEvent event){

        setDefaultLogFileName();

        appPreference.setString(MainViewModel.logHeaderDelimiter, delimiterField.getText());
        dataLogger = new DataLogger();
        List<DataSource> dataSources = viewModel.getRepository().getDataSources().stream().map(DataSourceViewModel::getDataSource).collect(Collectors.toList());
        try {
            dataLogger.startLogging(dataSources, logFolderField.getText(),fileName.getText()+"_"+logDescription.getText(), delimiterField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void stopLog(){
        if (dataLogger!=null) {
            try {
                dataLogger.stopLogging();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stop logging");
    }

    @FXML
    void openLogFolder(ActionEvent event) {
        String folderPath = appPreference.getString(MainViewModel.logFolderName);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (!folderPath.equals("")){
            directoryChooser.setInitialDirectory(new File(folderPath));
        }
        directoryChooser.setTitle("Папка сохранения логов");
        Optional<File> fileOptional = Optional.ofNullable(directoryChooser.showDialog(logFolderField.getScene().getWindow()));
        fileOptional.ifPresent(f->{
            logFolderField.setText(f.getAbsolutePath());
            appPreference.setString(MainViewModel.logFolderName, f.getAbsolutePath());
        });
    }

    @FXML
    void addDataSet(ActionEvent event) {
        final Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        final ViewTuple<AddDataSourceView, AddDataSourceViewModel> tuple = FluentViewLoader.fxmlView(AddDataSourceView.class).load();
        tuple.getCodeBehind().setStage(dialogStage);
        final Parent view = tuple.getView();
        Scene scene = new Scene(view);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    @FXML
    void startPlotting(ActionEvent event) {

        if (chartView!=null) {
            try {
                chartView.stopPlotting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        chartView = new ChartView("ЛИ АНПА");

        viewModel.getRepository().getDataSources().forEach(d->chartView.addDataSource(d));

        int timeRange = Integer.parseInt(timeRangeField.getText());

        chartView.init(timeRange);

        chartPane.getChildren().clear();
        chartPane.getChildren().add(chartView);

        AnchorPane.setBottomAnchor(chartView, 0.);
        AnchorPane.setTopAnchor(chartView, 0.);
        AnchorPane.setLeftAnchor(chartView, 0.);
        AnchorPane.setRightAnchor(chartView, 0.);

        chartView.startPlotting();


    }

    @FXML
    void fillDefaultDataSource(ActionEvent e){

        //TODO: deep refactor
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(getClass().getResource("/com/oceanos/fxdataplotter/view/descriptions/liDataSources.json"));
            root.elements().forEachRemaining(dataSourceNodeElem-> {

                JsonNode dataSourceNode = dataSourceNodeElem.get("dataSource");
                String dataSourceName = dataSourceNode.get("name").asText();
                String sampleData = dataSourceNode.get("sampleData").asText();
                JsonNode connectionNode = dataSourceNode.get("connection");
                String connectionType = connectionNode.get("type").asText();

                Connection connection = null;

                if (connectionType.equals("zeroMQ")){
                    String host = connectionNode.get("host").asText();
                    int port = connectionNode.get("port").asInt();
                    String topic = connectionNode.get("topic").asText();
                    connection = new ZeroMQConnection("tcp://"+host+":"+port, topic);
                }
                DataAdapter adapter = null;
                String dataAdapterType = dataSourceNode.get("dataAdapter").asText();
                if (dataAdapterType.equals("JSON")){
                    adapter = new JSONAdapter();
                }
                ObservableList<DataFieldViewModel> dataFieldViewModels = FXCollections.observableArrayList();
                List<DataField> dataFields = new ArrayList<>();

                JsonNode dataFieldsNode = dataSourceNodeElem.get("dataFieldViewModels");


                dataFieldsNode.elements().forEachRemaining(dataFieldNode->{

                    String dataFieldName = dataFieldNode.get("name").asText();
                    String dataFieldOldName = dataFieldNode.get("oldName").asText();
                    int dataFieldPosition = dataFieldNode.get("position").asInt();
                    double min = dataFieldNode.get("min").asDouble(0.);
                    double max = dataFieldNode.get("max").asDouble(0.);
                    boolean enabled = dataFieldNode.get("enabled").asBoolean();

                    JsonNode colorNode = dataFieldNode.get("color");

                    double red = colorNode.get("red").doubleValue();
                    double green = colorNode.get("green").doubleValue();
                    double blue = colorNode.get("blue").doubleValue();
                    double opacity = colorNode.get("opacity").doubleValue();

                    Color color = new Color(red, green, blue, opacity);


                    DataField dataField = new DataField(dataFieldName, dataFieldPosition);
                    dataField.setOldName(dataFieldOldName);

                    dataFields.add(dataField);

                    DataFieldViewModel dataFieldViewModel = new DataFieldViewModel(dataField);
                    dataFieldViewModel.setEnabled(enabled);
                    dataFieldViewModel.setMax(max);
                    dataFieldViewModel.setMin(min);
                    dataFieldViewModel.setColor(color);
                    dataFieldViewModels.add(dataFieldViewModel);
                });

                adapter.setFields(dataFields);
                DataSource dataSource = new DataSource(connection, adapter);
                dataSource.setName(dataSourceName);
                dataSource.start();

                DataSourceViewModel dataSourceViewModel = new DataSourceViewModel(dataSource, dataFieldViewModels);

                viewModel.getRepository().addDataSourceViewModel(dataSourceViewModel);


            });
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void initialize(){
        appPreference = viewModel.getPreference();
        logFolderField.setText(appPreference.getString(MainViewModel.logFolderName));
        delimiterField.setText(appPreference.getString(MainViewModel.logHeaderDelimiter));


        initTreeView();

        linePicker.setValue(Color.RED);
    }

    private void addLineNode(){
        DraggableNode node = new DraggableNode();
        node.setPrefHeight(2);
        Color color = linePicker.getValue();
        node.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        chartPane.getChildren().add(node);
        AnchorPane.setLeftAnchor(node,0.);
        AnchorPane.setRightAnchor(node, 0.);
    }

    private void initTreeView(){

        TreeItem<DataSourceViewModel> root = new TreeItem<>(new RootTreeDataSourceViewModel());
        dataSetTree.setRoot(root);
        dataSetTree.setShowRoot(false);
        root.setExpanded(true);

        dataSetTree.setCellFactory(p -> new TextFieldTreeCellImpl());

        viewModel.getRepository().getDataSources().addListener((InvalidationListener) observable -> {

            root.getChildren().clear();
            viewModel.getRepository().getDataSources().forEach(d->{
                TreeItem<DataSourceViewModel> treeItem = new TreeItem<>(d);
                root.getChildren().add(treeItem);
            });
        });

        dataSetTree.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<DataSourceViewModel>>) c -> {
           DataSourceViewModel selected = dataSetTree.getSelectionModel().getSelectedItem().getValue();
            ViewTuple viewTuple = FluentViewLoader.javaView(DataSourceTableView.class).load();
            DataSourceTableView view = (DataSourceTableView) viewTuple.getView();
            view.init(selected);
            dataFieldsPane.getChildren().clear();
            dataFieldsPane.getChildren().add(view);
        });

    }

    public void stop(){
        System.out.println("Stop Main View");
        if (chartView!=null) {
            try {
                chartView.stopPlotting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDefaultLogFileName(){
        Date date = new Date();
        SimpleDateFormat mdyFormat = new SimpleDateFormat("MM-dd-hh-mm");
        String formatted = mdyFormat.format(date);
        fileName.setText(formatted);
    }

    private class RootTreeDataSourceViewModel extends DataSourceViewModel {

        public RootTreeDataSourceViewModel() {
            //super();
        }

        @Override
        public String toString() {
            return "Data Sources";
        }
    }


}
