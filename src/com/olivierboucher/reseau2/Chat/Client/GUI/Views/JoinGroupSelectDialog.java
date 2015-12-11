package com.olivierboucher.reseau2.Chat.Client.GUI.Views;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Created by boucheol on 2015-12-11.
 */
public class JoinGroupSelectDialog extends TextInputDialog {
    public JoinGroupSelectDialog() {
        super();
        this.setTitle("Join a Group");
        this.setHeaderText("Please enter the group name.");
        Node connect = this.getDialogPane().lookupButton(ButtonType.OK);
        connect.setDisable(true);
        this.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            connect.setDisable(newValue.trim().isEmpty());
        });
    }
}
