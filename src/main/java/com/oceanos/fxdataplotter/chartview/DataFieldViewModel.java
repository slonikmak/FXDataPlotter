package com.oceanos.fxdataplotter.chartview;

import com.oceanos.fxdataplotter.model.DataField;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class DataFieldViewModel {

    private DoubleProperty min = new SimpleDoubleProperty(0);
    private DoubleProperty max = new SimpleDoubleProperty(1);
    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty position = new SimpleIntegerProperty();
    private BooleanProperty enabled = new SimpleBooleanProperty(true);
    private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.RED);
    private DataField dataField;
    private DoubleProperty currentValue = new SimpleDoubleProperty();

    public DataFieldViewModel(DataField dataField) {
        this.dataField = dataField;
        this.position.setValue(dataField.getPosition());
        this.name.setValue(dataField.getName());
        this.name.addListener((o,oV,nV)->dataField.setName(nV));
    }

    public DataFieldViewModel(String name, int position, DataField dataField) {
        this.name.setValue(name);
        this.position.setValue(position);
        this.dataField = dataField;

        this.name.addListener((o,oV,nV)->dataField.setName(nV));
    }

    public double getMin() {
        return min.get();
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public void setMin(double min) {
        this.min.set(min);
    }

    public double getMax() {
        return max.get();
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public void setMax(double max) {
        this.max.set(max);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getPosition() {
        return position.get();
    }

    public IntegerProperty positionProperty() {
        return position;
    }

    public void setPosition(int position) {
        this.position.set(position);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public DataField getDataField(){
        return dataField;
    }

    public double getCurrentValue() {
        return currentValue.get();
    }

    public DoubleProperty currentValueProperty() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue.set(currentValue);
    }
}
