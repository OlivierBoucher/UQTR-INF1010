package com.olivierboucher.reseau2.Chat.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by olivier on 2015-10-11.
 */
public class CommandParser {

    private Pattern regex;

    public CommandParser() {
        regex = Pattern.compile("([a-zA-Z0-9-_]+(\\:\\s))?([A-Z]+)\\s(\\#|\\$)([a-zA-Z0-9-_]+)\\s\\:(.+)?");
    }

    public Command interpretCommandString(String commandString) throws CommandParserException {
        //Group 0 : Full string
        //Group 1 : Sender - Optional
        //Group 2 : Ignore
        //Group 3 : Command verb
        //Group 4 : Target Identifier
        //Group 5 : Target name
        //Group 6 : Message
        Matcher m = regex.matcher(commandString);
        if (m.matches()) {
            Command cmd = new Command();
            cmd.setSender(m.group(1));
            cmd.setVerb(m.group(3));
            cmd.setTargetId("#".equals(m.group(4)) ? Command.CommandTarget.BROADCAST :
                            "$".equals(m.group(4)) ? Command.CommandTarget.PRIVATE :
                            null); //Null should never happen, in fact we could deduce "$" is "#" is false
            cmd.setTargetName(m.group(5));
            cmd.setMessage(m.group(6));

            return cmd;
        }

        throw new CommandParserException("CommandString is invalid");
    }
}


