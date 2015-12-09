package com.olivierboucher.reseau2.Chat.Client.GUI.Views;


import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Created by olivier on 2015-12-08.
 */
public class NicknameSelectDialog extends TextInputDialog{

    public NicknameSelectDialog(){
        super();
        this.setTitle("Identification");
        this.setHeaderText("Please choose a nickname.");
        this.getDialogPane().getButtonTypes().removeAll(ButtonType.CANCEL);
        Node connect = this.getDialogPane().lookupButton(ButtonType.OK);
        connect.setDisable(true);
        this.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            connect.setDisable(newValue.trim().isEmpty());
        });
    }
}
