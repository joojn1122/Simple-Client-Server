package com.joojn.server.Server;

import java.net.*;
import java.util.*;
import java.lang.*;

public class SocketServer {

	private int port;

    private boolean running = true;

    private ServerSocket serverSocket;

    public SocketServer(int port){
        this.port  = port;
    }

    private final Set<SocketClient> clientList = new HashSet<>();

	public void start() throws Exception{
		this.serverSocket = new ServerSocket(this.port);

        System.out.println("Listening at port " + port + "..");
        getInput();

        while(this.running){

            Socket clientSocket = serverSocket.accept();
            SocketClient client = new SocketClient(clientSocket);
            clientList.add(client);
            client.handle();

        }
	}
    public void removeClient(SocketClient client){
        this.clientList.remove(client);
        broadcast(null, String.format("%s left the server!", client.getUsername()), null);
    }

    public void broadcast(String username, String message, SocketClient dontSend){

        message = message.replace("\n", "");

        if(username == null) System.out.println(message);
        else System.out.println(String.format("%s > %s", username, message));

        for(SocketClient socketClient : clientList){
            if(socketClient.equals(dontSend)) continue;
            socketClient.setResponse("type", "message", "username", username, "message", message);
        }
    }

    public void getInput(){
        Thread thread = new Thread(){
            public void run(){
                Scanner sc = new Scanner(System.in);
                while(running){
                    // System.out.print("> ");
                    String input = sc.nextLine();

                    if(input.equalsIgnoreCase("exit")){
                        running = false;
                        try{
                            for(SocketClient client : clientList){
                                client.disconnect();
                            }
                            serverSocket.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        broadcast("server", input, null);
                    }
                }
            }
        };
        thread.start();
    }

}