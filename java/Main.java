package com.joojn.server;

import com.joojn.server.Server.SocketServer;

public class Main {

    public static SocketServer socketServer = new SocketServer(30000);

    public static void main(String[] args) {
        try{
            socketServer.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
