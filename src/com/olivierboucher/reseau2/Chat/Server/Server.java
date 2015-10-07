package com.olivierboucher.reseau2.Chat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olivier on 2015-10-06.
 */
public class Server implements IClientDelegate {
    private int port;
    private ServerSocket srvSocket;
    private List<ServerClient> clients;

    public Server(int port) throws IOException {
        this.port = port;
        this.srvSocket = new ServerSocket(port);
        this.clients = new ArrayList<>();
    }

    public void start() {
        while(true){
            try {
                clients.add(new ServerClient(srvSocket.accept(), this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void newCommandRecievedFromClient(ServerClient serverClient, Command command) {

    }
}
