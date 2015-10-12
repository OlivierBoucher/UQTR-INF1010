package com.olivierboucher.reseau2.Chat.Client.GUI.Controllers;

import com.olivierboucher.reseau2.Chat.Client.ChatClient;
import com.olivierboucher.reseau2.Chat.Client.IClientMessageHandler;
import com.olivierboucher.reseau2.Chat.ClientMain;
import com.olivierboucher.reseau2.Chat.Common.Command;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class MainController implements IClientMessageHandler {
    @FXML
    private ListView<String> chatListView;
    @FXML
    private TextArea input;
    @FXML
    private Button sendButton;

    private ObservableList<String> chatHistory = FXCollections.observableArrayList();
    private ChatClient chatClient;
    private ClientMain mainApp;

    public MainController() {
        try {
            chatClient = new ChatClient("127.0.0.1", 1337, this);
            chatClient.sendNickCommand("Olivier");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        chatListView.setItems(chatHistory);

        sendButton.setOnAction(evt -> {
            chatClient.sendCommand(input.getText());
            input.clear();
        });
    }

    public void setMainApp(ClientMain mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void handleCommand(Command command) {
        Platform.runLater(() -> {
            chatHistory.add(String.format("[%s]: %s", command.getSender(), command.getMessage().replace(Command.NEWLINE, "\r\n")));
        });
    }
}
