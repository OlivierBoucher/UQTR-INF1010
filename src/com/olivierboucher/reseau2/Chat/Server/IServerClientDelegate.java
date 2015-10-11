package com.olivierboucher.reseau2.Chat.Server;

import com.olivierboucher.reseau2.Chat.Common.Command;

/**
 * Created by olivier on 2015-10-06.
 */
public interface IServerClientDelegate {
    void newCommandRecievedFromClient(ServerClient serverClient, Command command);
}
