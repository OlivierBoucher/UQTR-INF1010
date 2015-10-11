package com.olivierboucher.reseau2.Chat.Server;

import com.olivierboucher.reseau2.Chat.Common.Command;
import com.olivierboucher.reseau2.Chat.Common.CommandParser;
import com.olivierboucher.reseau2.Chat.Common.CommandParserException;

import java.io.*;
import java.net.Socket;

/**
 * Created by olivier on 2015-10-06.
 */
public class ServerClient {
    private Socket connection;
    private IServerClientDelegate delegate;
    private Thread inputThread;
    private Boolean isRunning;
    private Boolean confirmed;
    private String nick;
    private CommandParser interpreter;
    private BufferedWriter bufferedWriter;

    public ServerClient(Socket connection, IServerClientDelegate delegate){
        this.connection = connection;
        this.delegate = delegate;
        this.interpreter = new CommandParser();
        this.confirmed = false;
        this.nick = "";
        startAcceptingInput();
    }

    public void closeConnection() throws IOException {
        isRunning = false;
        connection.close(); //NOTE(Olivier): This also closes the input/output streams
        try {
            //We leave 100ms to the thread to join gracely, then we kill it
            inputThread.join(100);
        }
        catch (InterruptedException e){
            //Do nothing, the thread has already been interrupted
        }
    }

    public void sendCommand(Command cmd) {
        try{
            bufferedWriter.write(cmd.toCommandString());
        }
        catch (IOException e) {
            //Cannot really do anything, just close the connection and remove the client
            //In the finally block
            System.out.println("IOException occured while writing");
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                //Connection is already closed
                delegate.removeHungClient(ServerClient.this);
            }
        }
    }

    private void startAcceptingInput(){
        isRunning = true;
        inputThread = new Thread() {
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

                    while(isRunning){
                        String cmdString;
                        if((cmdString = reader.readLine()) != null){
                            try {
                                delegate.newCommandRecievedFromClient(ServerClient.this, interpreter.interpretCommandString(cmdString));
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
                } finally {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        //Connection is already closed
                        delegate.removeHungClient(ServerClient.this);
                    }
                }
            }
        };

        inputThread.start();
    }

    private void stopRecievingInput(){
        isRunning = false;
    }


    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
