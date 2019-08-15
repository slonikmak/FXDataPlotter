package com.oceanos.fxdataplotter.preference;

import java.util.prefs.Preferences;

/**
 * @autor slonikmak on 14.08.2019.
 */
public class AppPreference {

    private Preferences preferences;

    public AppPreference(){
        preferences = Preferences.userRoot().node("fxdataplotter");
    }

    public int getInt(String propName){
        return preferences.getInt(propName, 0);
    }

    public String getString(String propName){
        return preferences.get(propName, "");
    }

    public double getDouble(String propName){
        return preferences.getDouble(propName, 0.);
    }

    public void setInt(String propName, int value){
        preferences.putInt(propName, value);
    }

    public void setDouble(String propName, double value){
        preferences.putDouble(propName, value);
    }
    public void setString(String propName, String value){
        preferences.put(propName, value);
    }
}
