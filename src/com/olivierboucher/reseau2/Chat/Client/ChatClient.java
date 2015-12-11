package com.olivierboucher.reseau2.Chat.Client;

import com.olivierboucher.reseau2.Chat.Common.Command;
import com.olivierboucher.reseau2.Chat.Common.CommandParser;
import com.olivierboucher.reseau2.Chat.Common.CommandParserException;

import java.io.*;
import java.net.Socket;

/**
 * Created by olivier on 2015-10-06.
 */
public class ChatClient {
    private Socket connection;
    private String nick;
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
                                messageHandler.handleCommand(parser.parseCommandString(cmdString));
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

    public void disconnect() {
        if(connection.isConnected()){
            keepAlive = false;
            readingThread.interrupt();
            sendQuitCommand();
        }
    }

    public void sendCommand(Command cmd){
        try {
            writer.write(cmd.toCommandString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendQuitCommand(){
        Command cmd = new Command();
        cmd.setVerb(Command.DISCONNECT_CMD);
        cmd.setMessage("");
        sendCommand(cmd);
    }

    public void sendCommand(String command){

        Command cmd = parser.parseClientSyntax(command);
        sendCommand(cmd);
    }

    public void sendNickCommand(String nick) {
        this.nick = nick;
        Command cmd = new Command();
        cmd.setVerb(Command.NICK_CMD);
        cmd.setMessage(nick);
        sendCommand(cmd);
    }

    public String getNick() {
        return nick;
    }
    public Boolean getKeepAlive() {
        return keepAlive;
    }
}
