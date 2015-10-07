package com.olivierboucher.reseau2.Chat.Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by olivier on 2015-10-06.
 */
public class ChatClient {
    private Socket connection;
    private BufferedWriter writer;
    private Boolean keepAlive;

    public ChatClient(String host, int port) throws IOException {
        keepAlive = true;
        connection = new Socket(host, port);
        writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
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
