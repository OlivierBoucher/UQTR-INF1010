package com.olivierboucher.reseau2.Chat.Client;

import com.olivierboucher.reseau2.Chat.Common.Command;

/**
 * Created by olivier on 2015-10-11.
 */
public interface IClientMessageHandler {
    public void handleCommand(Command command);
}
