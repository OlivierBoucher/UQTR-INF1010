package com.olivierboucher.reseau2.Chat.Client;

import com.olivierboucher.reseau2.Chat.Common.CommandParser;
import com.olivierboucher.reseau2.Chat.Common.CommandParserException;

import java.io.*;
import java.net.Socket;

/**
 * Created by olivier on 2015-10-06.
 */
public class ChatClient {
    private Socket connection;
    private BufferedWriter writer;
    private Boolean keepAlive;
    private Thread readingThread;
    private CommandParser parser;
    private IClientMessageHandler messageHandler;

    public ChatClient(String host, int port, IClientMessageHandler msgHandler) throws IOException {
        keepAlive = true;
        connection = new Socket(host, port);
        writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        parser = new CommandParser();
        messageHandler = msgHandler;

        readingThread = new Thread() {
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while(keepAlive){
                        String cmdString;
                        if((cmdString = reader.readLine()) != null){
                            try {
                                msgHandler.handleCommand(parser.interpretCommandString(cmdString));
                            } catch (CommandParserException e) {
                                //Ignore the command
                                System.out.println("Recieved an invalid commandString");
                            }
                        }
                    }

                } catch (IOException e) {
                    //Cannot really do anything, just close the connection and remove the client
                    //In the finally block
                    System.out.println("IOException occured while reading");
                    try {
                        connection.close();
                    } catch (IOException ee) {
                        //Connection is already closed
                    }
                }

                System.out.println("Exited read loop");
            }
        };

        readingThread.start();
    }

    public void sendCommand(String command){
        try {
            writer.write(command);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }
}
