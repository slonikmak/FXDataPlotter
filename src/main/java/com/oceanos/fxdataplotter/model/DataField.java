package com.oceanos.fxdataplotter.model;

/**
 * @autor slonikmak on 12.08.2019.
 */
public class DataField {

    private String oldName;
    private String name;
    private int position;
    private Double value;

    public DataField(String name, int position){
        this.name = name;
        this.oldName = name;
        this.position = position;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getOldName(){
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
