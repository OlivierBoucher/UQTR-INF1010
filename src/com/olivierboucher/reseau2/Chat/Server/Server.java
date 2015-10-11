package com.olivierboucher.reseau2.Chat.Server;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by olivier on 2015-10-06.
 */
public class Server implements IServerClientDelegate {
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

        switch (command.getVerb()){
            case "NICK":
                String desiredNick = command.getMessage();
                Boolean taken = false;

                //Use synchronized for concurrency since we can accept new clients at any time
                synchronized (this){
                    taken = clients
                           .stream()
                           .filter(x -> x.getNick().equalsIgnoreCase(desiredNick) && x.getConfirmed())
                           .findAny()
                           .isPresent();
                }

                if(!taken) {
                    serverClient.setNick(desiredNick);
                    serverClient.setConfirmed(true);
                }
                else {
                    //TODO(Olivier): Send nick taken error
                }
                break;

            case "DISCONNECT":
                if(serverClient.getConfirmed()){
                    StringBuilder message = new StringBuilder();
                    message.append(serverClient.getNick());

                    if(command.getMessage() == null || command.getMessage().equalsIgnoreCase("")) {
                        message.append("has disconnected.");
                    }
                    else {
                        message.append(" has disconnected with message: ");
                        message.append(command.getMessage());
                    }

                    //TODO(Olivier): Broadcast the message to everyone
                }

                try {
                    serverClient.closeConnection();
                }
                catch (IOException e) {
                    //DO nothing, connection is already closed, just proceed to removal
                }
                finally {
                    //Again for list concurrency
                    synchronized (this){
                        clients.remove(serverClient);
                    }
                }

                break;

            case "MSG":
                if(serverClient.getConfirmed()){
                    //TODO(Olivier): Broadcast the message to everyone
                }
                else {
                    //TODO(Olivier): Send error must acquire nick prior
                }
                break;

            default:
                //TODO(Olivier): Send error unknown command
                break;
        }
    }
}
