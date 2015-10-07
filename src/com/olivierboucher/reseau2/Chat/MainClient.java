package com.olivierboucher.reseau2.Chat;

import com.olivierboucher.reseau2.Chat.Client.ChatClient;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by olivier on 2015-10-06.
 */
public class MainClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            ChatClient client = new ChatClient("127.0.0.1", 1337);
            while(client.getKeepAlive()){
                if (scanner.hasNext()){
                    String cmdString = scanner.nextLine();
                    client.sendCommand(cmdString);
                }
            }

        } catch (IOException e) {
            System.out.println("Client exited with the following error:");
            e.printStackTrace();
        }
    }
}
