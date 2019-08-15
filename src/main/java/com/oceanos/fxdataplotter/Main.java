package com.oceanos.fxdataplotter;

import com.google.inject.Module;
import com.oceanos.fxdataplotter.view.MainView;
import com.oceanos.fxdataplotter.viewmodel.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Main extends MvvmfxGuiceApplication {

    private MainViewModel mainViewModel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initMvvmfx() throws Exception {

    }

    @Override
    public void startMvvmfx(Stage stage) throws Exception {

        final ViewTuple<MainView, MainViewModel> tuple = FluentViewLoader.fxmlView(MainView.class).load();

        mainViewModel = tuple.getViewModel();

        final Parent view = tuple.getView();

        final Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stopMvvmfx() throws Exception {
        mainViewModel.stop();
    }

    @Override
    public void initGuiceModules(List<Module> modules) throws Exception {
        super.initGuiceModules(modules);
        modules.add(new GuiceMainModule());
    }
}
