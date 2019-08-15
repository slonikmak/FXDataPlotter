package com.oceanos.fxdataplotter.tests.ui;

import com.google.inject.Module;
import com.oceanos.fxdataplotter.GuiceMainModule;
import com.oceanos.fxdataplotter.view.AddDataSourceView;
import com.oceanos.fxdataplotter.view.MainView;
import com.oceanos.fxdataplotter.viewmodel.AddDataSourceViewModel;
import com.oceanos.fxdataplotter.viewmodel.MainViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class AddDataSourceViewTest extends MvvmfxGuiceApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initMvvmfx() throws Exception {

    }

    @Override
    public void startMvvmfx(Stage stage) throws Exception {

        final ViewTuple<AddDataSourceView, AddDataSourceViewModel> tuple = FluentViewLoader.fxmlView(AddDataSourceView.class).load();

        final Parent view = tuple.getView();

        final Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stopMvvmfx() throws Exception {

    }

    @Override
    public void initGuiceModules(List<Module> modules) throws Exception {
        super.initGuiceModules(modules);
        modules.add(new GuiceMainModule());
    }
}
