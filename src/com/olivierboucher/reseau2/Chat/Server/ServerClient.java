package com.olivierboucher.reseau2.Chat.Server;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by olivier on 2015-10-06.
 */
public class ServerClient {
    private Socket connection;
    private IClientDelegate delegate;
    private Thread inputThread;
    private Boolean isRunning;

    public ServerClient(Socket connection, IClientDelegate delegate){
        this.connection = connection;
        this.delegate = delegate;
        startAcceptingInput();
    }

    public void closeConnection() throws IOException {
        isRunning = false;
        connection.close();
    }

    private void startAcceptingInput(){
        isRunning = true;
        inputThread = new Thread() {
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while(isRunning){
                        String cmdString;
                        if((cmdString = reader.readLine()) != null){
                            System.out.println(cmdString);
                        }
                    }

                } catch (IOException e) {

                } finally {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        //Connection is already closed
                        //TODO: Send command to delegate to remove from list
                        e.printStackTrace();
                    }
                }
            }
        };

        inputThread.start();
    }

    private void stopRecievingInput(){
        isRunning = false;
    }

}
