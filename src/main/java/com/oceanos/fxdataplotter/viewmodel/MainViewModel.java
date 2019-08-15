package com.oceanos.fxdataplotter.viewmodel;

import com.google.inject.Inject;
import com.oceanos.fxdataplotter.model.Repository;
import com.oceanos.fxdataplotter.preference.AppPreference;
import de.saxsys.mvvmfx.ViewModel;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class MainViewModel implements ViewModel {

    public final static String logFolderName = "logFolderName";
    public final static String logHeaderDelimiter = "logHeaderDelimiter";


    @Inject
    Repository repository;

    @Inject
    AppPreference preference;

    public Repository getRepository() {
        return repository;
    }

    public AppPreference getPreference(){
        return preference;
    }

    public void stop(){
        repository.getDataSources().forEach(d->d.getDataSource().stop());
    }
}
