package com.olivierboucher.reseau2.Chat;

import com.olivierboucher.reseau2.Chat.Client.GUI.Controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ClientMain extends Application {
    public static String serverIP = "";
    private Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chat Client");

        initRootLayout();
    }

    public void initRootLayout() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Client/GUI/Views/mainView.fxml"));

            rootLayout = loader.load();

            MainController ctrl = loader.getController();
            ctrl.setMainApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(t -> {
                ctrl.willExit();
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean validIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        ip = ip.trim();
        if ((ip.length() < 6) & (ip.length() > 15)) return false;

        try {
            Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
    public static void main(String[] args) {
        if(args.length != 1 && !validIP(args[0])){
            System.out.println("You must provide the server's IP address as parameter.");
            System.exit(1);
        }
        serverIP = args[0];
        launch(args);
    }
}
