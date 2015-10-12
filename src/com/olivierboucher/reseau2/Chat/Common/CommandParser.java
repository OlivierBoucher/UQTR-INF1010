package com.olivierboucher.reseau2.Chat.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by olivier on 2015-10-11.
 */
public class CommandParser {

    private Pattern commandStringRegex;
    private Pattern privateMessageRegex;

    public CommandParser() {
        commandStringRegex = Pattern.compile("(([a-zA-Z0-9-_]+)(\\:\\s))?([A-Z0-9]+)(\\s(\\#|\\$)([a-zA-Z0-9-_]+))?\\s\\:(.+)?");
        privateMessageRegex = Pattern.compile("\\/w\\s([a-zA-Z0-9-_]+)\\s(.+)");
    }

    public Command parseCommandString(String commandString) throws CommandParserException {
        //Group 0 : Full string
        //Group 1 : Ignore
        //Group 2 : Sender - Optional
        //Group 3 : Ignore
        //Group 4 : Command verb
        //Group 5 : Ignore
        //Group 6 : Target Identifier
        //Group 7 : Target name
        //Group 8 : Message
        Matcher m = commandStringRegex.matcher(commandString);
        if (m.matches()) {
            Command cmd = new Command();
            cmd.setSender(m.group(2));
            cmd.setVerb(m.group(4));
            cmd.setTargetId("#".equals(m.group(6)) ? Command.CommandTarget.BROADCAST :
                            "$".equals(m.group(6)) ? Command.CommandTarget.PRIVATE :
                            null); //Null should never happen, in fact we could deduce "$" is "#" is false
            cmd.setTargetName(m.group(7));
            cmd.setMessage(m.group(8));

            return cmd;
        }

        throw new CommandParserException("CommandString is invalid");
    }

    public Command parseClientSyntax(String input) {
        //Sanitize
        String sanitized = input.replace("\n", Command.NEWLINE);

        Command cmd = new Command();
        Matcher privMsgMatcher = privateMessageRegex.matcher(input);
        if(privMsgMatcher.matches()){
            //Private message
            cmd.setVerb(Command.MSG_CMD);
            cmd.setTargetId(Command.CommandTarget.PRIVATE);
            cmd.setTargetName(privMsgMatcher.group(1));
            cmd.setMessage(privMsgMatcher.group(2));
        }
        else if(input.equalsIgnoreCase("/list")){
            //List request
            cmd.setVerb(Command.LIST_CMD);
            cmd.setMessage("");
        }
        else if(input.toLowerCase().startsWith("/disconnect ")) {
            //Disconnect
            cmd.setVerb(Command.DISCONNECT_CMD);
            cmd.setMessage(sanitized.substring(12));
        }
        else {
            //Broadcast msg
            cmd.setVerb(Command.MSG_CMD);
            cmd.setTargetId(Command.CommandTarget.BROADCAST);
            cmd.setTargetName("ALL");
            cmd.setMessage(sanitized);
        }

        return cmd;
    }
}


