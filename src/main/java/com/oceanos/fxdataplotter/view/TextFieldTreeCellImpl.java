package com.oceanos.fxdataplotter.view;

import com.oceanos.fxdataplotter.chartview.DataSourceViewModel;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;

/**
 * @autor slonikmak on 13.08.2019.
 */
class TextFieldTreeCellImpl extends TreeCell<DataSourceViewModel> {

    private Label label;

    public TextFieldTreeCellImpl() {

    }


    @Override
    public void updateItem(DataSourceViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
                setText(item.toString());
                setGraphic(null);
        }
    }

   /* private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });
    }*/

}