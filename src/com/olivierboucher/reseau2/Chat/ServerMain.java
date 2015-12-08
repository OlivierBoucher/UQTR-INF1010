package com.olivierboucher.reseau2.Chat;

import com.olivierboucher.reseau2.Chat.Server.Server;

import java.io.IOException;

/**
 * Created by olivier on 2015-10-06.
 */
public class ServerMain {

    private static final int PORT = 1337;

    public static void main(String[] args) {

        System.out.println("Starting server on port " + PORT);
        try {
            Server chatServer = new Server(PORT);
            chatServer.start();
        } catch (IOException e) {
            System.out.println("Could not start server on port " + PORT + " please check that it is available");
        }
    }
}
