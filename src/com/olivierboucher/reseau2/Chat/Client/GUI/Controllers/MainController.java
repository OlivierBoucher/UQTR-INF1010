package com.olivierboucher.reseau2.Chat.Client.GUI.Controllers;

import com.olivierboucher.reseau2.Chat.Client.ChatClient;
import com.olivierboucher.reseau2.Chat.Client.GUI.Views.JoinGroupSelectDialog;
import com.olivierboucher.reseau2.Chat.Client.GUI.Views.NicknameSelectDialog;
import com.olivierboucher.reseau2.Chat.Client.IClientMessageHandler;
import com.olivierboucher.reseau2.Chat.ClientMain;
import com.olivierboucher.reseau2.Chat.Common.Command;
import com.olivierboucher.reseau2.Chat.Server.Server;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.io.IOException;
import java.security.cert.PKIXRevocationChecker;
import java.util.Optional;

public class MainController implements IClientMessageHandler {
    @FXML
    private ListView<String> chatListView;
    @FXML
    private ListView<String> groupsListView;
    @FXML
    private TextArea input;
    @FXML
    private Button sendButton;
    @FXML
    private Button joinGroup;

    private ObservableList<String> chatHistory = FXCollections.observableArrayList();
    private ObservableList<String> joinedGroups = FXCollections.observableArrayList();
    private ChatClient chatClient;
    private ClientMain mainApp;

    public MainController() {
        try {
            chatClient = new ChatClient(ClientMain.serverIP, 1337, this);

            promptForNick();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void promptForNick(){
        TextInputDialog dialog = new NicknameSelectDialog();

        Optional<String> nickname = dialog.showAndWait();

        if(!nickname.isPresent()){
            System.out.println("Failed to choose a nickname");
            System.exit(0);
        }

        chatClient.sendNickCommand(nickname.get());
    }

    @FXML
    private void initialize() {
        chatListView.setItems(chatHistory);
        groupsListView.setItems(joinedGroups);

        sendButton.setOnAction(evt -> {
            if(!input.getText().trim().isEmpty()){
                chatClient.sendCommand(input.getText());
                input.clear();
            }
        });

        joinGroup.setOnAction(evt -> {
            JoinGroupSelectDialog dialog = new JoinGroupSelectDialog();
            Optional<String> groupNameOpt = dialog.showAndWait();

            if(groupNameOpt.isPresent()){
                String groupName = groupNameOpt.get().toUpperCase();
                if(!joinedGroups.contains(groupName)) {
                    joinedGroups.add(groupName);
                    //TODO(Olivier): Send the actual group request
                    Command joinCmd = Command.getJoinCommandForGroup(groupName);
                    chatClient.sendCommand(joinCmd);
                }
            }
        });
    }

    public void setMainApp(ClientMain mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void handleCommand(Command command) {
        switch(command.getVerb()){
            case Command.MSG_CMD:
                if(command.getTargetId() == Command.CommandTarget.PRIVATE){
                    Platform.runLater(() -> {
                        if(command.getSender().equalsIgnoreCase(Server.SERVER_NICK)){
                            chatHistory.add(String.format("[%s]: %s", command.getSender(), command.getMessage().replace(Command.NEWLINE, "\r\n")));
                        }
                        else if(command.getSender().equalsIgnoreCase(chatClient.getNick())){
                            chatHistory.add(String.format("You whispered to [%s]: %s", command.getTargetName(), command.getMessage().replace(Command.NEWLINE, "\r\n")));
                        }
                        else {
                            chatHistory.add(String.format("PRIVATE [%s]: %s", command.getSender(), command.getMessage().replace(Command.NEWLINE, "\r\n")));
                        }
                    });
                }
                else {
                    Platform.runLater(() -> {
                        String msg = command.getSender().equalsIgnoreCase(Server.SERVER_NICK) ?
                                String.format("[%s]: %s", command.getSender(), command.getMessage().replace(Command.NEWLINE, "\r\n")) :
                                String.format("#%s [%s]: %s", command.getTargetName(), command.getSender(), command.getMessage().replace(Command.NEWLINE, "\r\n"));
                        chatHistory.add(msg);
                    });
                }
                break;
            case Command.NICK_TAKEN_ERROR:
                promptForNick();
                break;
        }
    }

    public void willExit(){
        chatClient.disconnect();
    }
}
