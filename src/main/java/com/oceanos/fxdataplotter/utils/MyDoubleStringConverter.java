package com.oceanos.fxdataplotter.utils;

import javafx.util.converter.DoubleStringConverter;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class MyDoubleStringConverter extends DoubleStringConverter {
    @Override
    public Double fromString(final String value) {
        return value.isEmpty() ? null :
                Double.parseDouble(value);
    }

}