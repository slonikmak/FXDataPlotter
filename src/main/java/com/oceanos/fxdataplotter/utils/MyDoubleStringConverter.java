package com.oceanos.fxdataplotter.utils;

import javafx.util.converter.DoubleStringConverter;

/**
 * @autor slonikmak on 13.08.2019.
 */
public class MyDoubleStringConverter extends DoubleStringConverter {
    @Override
    public Double fromString(final String value) {
        return value.isEmpty() || !isNumber(value) ? null :
                super.fromString(value);
    }
    public boolean isNumber(String value) {
        int size = value.length();
        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return size > 0;
    }
}