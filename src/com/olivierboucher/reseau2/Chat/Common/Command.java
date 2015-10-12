package com.olivierboucher.reseau2.Chat.Common;

import com.olivierboucher.reseau2.Chat.Server.Server;

/**
 * Created by olivier on 2015-10-06.
 */
public class Command {
    //Special strings
    public static final String NEWLINE = "|NEWLINE|";
    //Error command verbs
    public static final String NICK_TAKEN_ERROR = "100";
    public static final String TARGET_NOT_FOUND_ERROR = "101";
    public static final String CLIENT_UNCONFIRMED_ERROR = "102";
    public static final String COMMAND_NOT_FOUND_ERROR = "103";
    //Basic command verbs
    public static final String NICK_CMD = "NICK";
    public static final String DISCONNECT_CMD = "DISCONNECT";
    public static final String MSG_CMD = "MSG";
    public static final String LIST_CMD = "LIST";

    public static Command getNickTakenCommand(String nick) {
        Command cmd = new Command();
        cmd.setVerb(NICK_TAKEN_ERROR);
        cmd.setSender(Server.SERVER_NICK);
        cmd.setMessage(nick);

        return cmd;
    }

    public static Command getDisconnectCommand(String message) {
        Command cmd = new Command();
        cmd.setVerb(MSG_CMD);
        cmd.setSender(Server.SERVER_NICK);
        cmd.setMessage(message);

        return cmd;
    }

    public static Command getTargetNotFoundError(String targetName) {
        Command cmd = new Command();
        cmd.setVerb(TARGET_NOT_FOUND_ERROR);
        cmd.setSender(Server.SERVER_NICK);

        StringBuilder message = new StringBuilder();
        message.append("Unknown target: \"");
        message.append(targetName);
        message.append("\". Cannot deliver message.");

        cmd.setMessage(message.toString());

        return cmd;
    }

    public static Command getUnconfirmedClientError() {
        Command cmd = new Command();
        cmd.setVerb(CLIENT_UNCONFIRMED_ERROR);
        cmd.setSender(Server.SERVER_NICK);
        cmd.setMessage("You must acquire a nick.");

        return cmd;
    }

    public static Command getCommandNotFoundError(String command) {
        Command cmd = new Command();
        cmd.setVerb(COMMAND_NOT_FOUND_ERROR);
        cmd.setSender(Server.SERVER_NICK);

        StringBuilder message = new StringBuilder();
        message.append("Command \"");
        message.append(command);
        message.append("\" is not recognized by the server.");

        cmd.setMessage(message.toString());

        return cmd;
    }

    public enum CommandTarget {
        BROADCAST("#"),
        PRIVATE("$");

        private String prefix;

        CommandTarget(String s){
            prefix = s;
        }

        public String getPrefix(){
            return prefix;
        }
    }

    private String sender;
    private String verb;
    private CommandTarget targetId;
    private String targetName;
    private String message;


    public String toCommandString() {
        if(verb != null && message != null){
            StringBuilder cmd = new StringBuilder();

            if(sender != null) {
                cmd.append(sender);
                cmd.append(": ");
            }

            cmd.append(verb.toUpperCase());
            cmd.append(" ");

            if(targetId != null && targetName != null) {
                cmd.append(targetId.getPrefix());
                cmd.append(targetName);
                cmd.append(" :");
                cmd.append(message);
            }
            else {
                cmd.append(":");
                cmd.append(message);
            }



            return cmd.toString();
        }
        return null;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public CommandTarget getTargetId() {
        return targetId;
    }

    public void setTargetId(CommandTarget targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
