package com.oceanos.fxdataplotter.view;

import com.google.inject.Inject;
import com.oceanos.fxdataplotter.chartview.ChartView;
import com.oceanos.fxdataplotter.data_source.DataSource;
import com.oceanos.fxdataplotter.logger.DataLogger;
import com.oceanos.fxdataplotter.preference.AppPreference;
import com.oceanos.fxdataplotter.viewmodel.AddDataSourceViewModel;
import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import com.oceanos.fxdataplotter.viewmodel.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    private void startLogging(ActionEvent event){

        setDefaultLogFileName();

        appPreference.setString(MainViewModel.logHeaderDelimiter, delimiterField.getText());
        dataLogger = new DataLogger();
        List<DataSource> dataSources = viewModel.getRepository().getDataSources().stream().map(DataSourceViewModel::getDataSource).collect(Collectors.toList());
        try {
            dataLogger.startLogging(dataSources, logFolderField.getText(),fileName.getText(), delimiterField.getText());
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

        chartView = new ChartView("Данные ЛИ АНПА");

        viewModel.getRepository().getDataSources().forEach(d->chartView.addDataSource(d));

        chartView.init(5000);

        chartPane.getChildren().clear();
        chartPane.getChildren().add(chartView);
        AnchorPane.setBottomAnchor(chartView, 0.);
        AnchorPane.setTopAnchor(chartView, 0.);
        AnchorPane.setLeftAnchor(chartView, 0.);
        AnchorPane.setRightAnchor(chartView, 0.);

    }

    public void initialize(){
        appPreference = viewModel.getPreference();
        logFolderField.setText(appPreference.getString(MainViewModel.logFolderName));
        delimiterField.setText(appPreference.getString(MainViewModel.logHeaderDelimiter));

        initTreeView();
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
