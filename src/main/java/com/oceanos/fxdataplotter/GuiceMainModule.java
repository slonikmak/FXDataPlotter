package com.oceanos.fxdataplotter;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.oceanos.fxdataplotter.model.Repository;
import com.oceanos.fxdataplotter.preference.AppPreference;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class GuiceMainModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(Repository.class).toInstance(new Repository());
        bind(AppPreference.class).toInstance(new AppPreference());
    }

}
