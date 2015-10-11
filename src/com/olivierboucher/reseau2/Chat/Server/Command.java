package com.olivierboucher.reseau2.Chat.Server;

/**
 * Created by olivier on 2015-10-06.
 */
public class Command {

    public enum CommandTarget {
        BROADCAST,
        PRIVATE
    }

    private String sender;
    private String verb;
    private CommandTarget targetId;
    private String targetName;
    private String message;

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
