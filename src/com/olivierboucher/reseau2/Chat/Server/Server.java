package com.olivierboucher.reseau2.Chat.Server;

import com.olivierboucher.reseau2.Chat.Common.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Created by olivier on 2015-10-06.
 */
public class Server implements IServerClientDelegate {
    public static final String SERVER_NICK = "SERVER";
    private int port;
    private ServerSocket srvSocket;
    private List<ServerClient> clients;
    private Map<String, List<ServerClient>> groupsMap;

    public Server(int port) throws IOException {
        this.port = port;
        this.srvSocket = new ServerSocket(port);
        this.clients = new ArrayList<>();
        this.groupsMap = new HashMap<>();
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

        System.out.println(String.format("Handling cmd: %s", command.toCommandString()));
        switch (command.getVerb()){
            case Command.NICK_CMD:
                String desiredNick = command.getMessage();
                Boolean taken = false;

                //Use synchronized for concurrency since we can accept new clients at any time
                synchronized (this){
                    taken = clients
                           .stream()
                           .filter(x -> (
                                   x.getNick().equalsIgnoreCase(desiredNick) ||
                                   x.getNick().equalsIgnoreCase(SERVER_NICK)) &&
                                   x.getConfirmed()
                           )
                           .findAny()
                           .isPresent();
                }

                if(!taken) {
                    serverClient.setNick(desiredNick);
                    serverClient.setConfirmed(true);
                    System.out.println(String.format("Nick confirmed %s", desiredNick));
                }
                else {
                    serverClient.sendCommand(Command.getNickTakenCommand(desiredNick));
                    break;
                }
            case Command.LIST_CMD:
                //List concurrency again..
                StringBuilder message = new StringBuilder();
                message.append("There is ");

                synchronized (this) {
                    long onlineCount = clients
                            .stream()
                            .filter(ServerClient::getConfirmed)
                            .count();

                    message.append(onlineCount);
                    message.append(String.format("%s", onlineCount > 1 ? " persons online." : " person online."));
                    message.append(Command.NEWLINE);

                    clients.stream()
                            .filter(ServerClient::getConfirmed)
                            .forEach(x -> {
                                System.out.println(String.format("List %s", x.getNick()));
                                message.append(x.getNick());
                                message.append(Command.NEWLINE);
                            });
                }
                System.out.println(String.format("Message is %s", message.toString()));
                Command cmd = new Command();
                cmd.setSender(SERVER_NICK);
                cmd.setVerb(Command.MSG_CMD);
                cmd.setMessage(message.toString());
                cmd.setTargetId(Command.CommandTarget.PRIVATE);
                cmd.setTargetName(serverClient.getNick());
                System.out.println(cmd.toCommandString());
                serverClient.sendCommand(cmd);
                break;

            case Command.DISCONNECT_CMD:
                if(serverClient.getConfirmed()){
                    message = new StringBuilder();
                    message.append(serverClient.getNick());

                    if(command.getMessage() == null || command.getMessage().equalsIgnoreCase("")) {
                        message.append(" has disconnected.");
                    }
                    else {
                        message.append(" has disconnected with message: ");
                        message.append(command.getMessage());
                    }

                    cmd = Command.getDisconnectCommand(message.toString());

                    //List concurrency once again
                    synchronized (this) {
                        for(ServerClient client : clients) {
                            client.sendCommand(cmd);
                        }
                    }
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

            case Command.MSG_CMD:
                if(serverClient.getConfirmed()){
                    command.setSender(serverClient.getNick());
                    if(command.getTargetId() == Command.CommandTarget.PRIVATE){
                        String targetNick = command.getTargetName();
                        Optional<ServerClient> foundTarget;

                        synchronized (this){
                            foundTarget = clients
                                    .stream()
                                    .filter(x -> x.getNick().equalsIgnoreCase(targetNick) && x.getConfirmed())
                                    .findAny();
                        }

                        if(foundTarget.isPresent()){
                            foundTarget.get().sendCommand(command);
                            serverClient.sendCommand(command);
                        }
                        else {
                            //Send target not found error
                            serverClient.sendCommand(Command.getTargetNotFoundError(serverClient.getNick()));
                        }
                    }
                    else {
                        if(command.getTargetName().equalsIgnoreCase("all")){
                            //Broadcast to everyone
                            //List concurrency once again
                            System.out.println(String.format("Broadcasting: %s", command.getMessage()));
                            synchronized (this) {
                                clients.stream()
                                        .filter(ServerClient::getConfirmed)
                                        .forEach(x -> x.sendCommand(command));
                            }
                        }
                        else {
                            String group = command.getTargetName().toUpperCase();
                            synchronized (this){
                                List<ServerClient> clientsInGroup = groupsMap.get(group);
                                if(clientsInGroup != null && clientsInGroup.contains(serverClient)){
                                    clients.stream()
                                            .filter(x -> x.getConfirmed() && clientsInGroup.contains(x))
                                            .forEach(x -> x.sendCommand(command));
                                }
                            }
                        }
                    }
                }
                else {
                    //Client is not confirmed, has no nick
                    serverClient.sendCommand(Command.getUnconfirmedClientError());
                }
                break;

            case Command.JOIN_CMD:
                String group = command.getMessage().toUpperCase();
                //For map concurrency
                synchronized (this){
                    List<ServerClient> clientsFromGroup = groupsMap.get(group);
                    if(clientsFromGroup == null){
                        //The group does not exist yet
                        groupsMap.put(group, new ArrayList<ServerClient>(){{
                            add(serverClient);
                        }});
                    }
                    else {
                        //If the client is not already in the group, add him
                        if(!clientsFromGroup.contains(serverClient)){
                            clientsFromGroup.add(serverClient);
                            groupsMap.put(group, clientsFromGroup);

                            //TODO(Olivier): Broadcast to everyone in the group that a new person joined
                        }
                    }
                }

                break;
            default:
                //Command not found
                serverClient.sendCommand(Command.getCommandNotFoundError(command.getVerb()));
                break;
        }
    }

    @Override
    public void removeHungClient(ServerClient serverClient) {
        //Again for list concurrency
        System.out.println("Removing hung client");
        synchronized (this){
            clients.remove(serverClient);
        }
    }

    @Override
    public void addClientToGroup(ServerClient serverClient, String group) {

    }
}
