package com.joojn.server.Server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.joojn.server.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

    private String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = true;

    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.requestUsername();
    }

    public void requestUsername() throws IOException {
        setResponse("type", "request", "value", "username");
        JsonObject json = new JsonParser().parse(in.readLine()).getAsJsonObject();
        this.username = json.get("username").getAsString();
    }

    public void disconnect() throws IOException{
        this.running = false;
        Main.socketServer.removeClient(this);
        this.out.close();
        this.in.close();
        this.socket.close();
    }

    public void handle(){
        Main.socketServer.broadcast(null, String.format("%s joined the server!", this.username), null);
        Thread thread = new Thread(){
            public void run(){
                while(running){
                    try {
                        String message = in.readLine();
                        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
                        String type = json.get("type").getAsString();

                        if(type.equalsIgnoreCase("message")){

                            String msg = json.get("message").getAsString();
                            Main.socketServer.broadcast(username, msg, null);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            disconnect();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    public String getUsername(){
        return this.username;
    }

    public void setResponse(String... responses){
        StringBuilder json = new StringBuilder("{ ");

        for(int i=0;i<responses.length;i+=2){
            String resp = responses[i];
            String value = responses[i+1];

            json.append(i == 0 ? "" : ",");
            json.append('"').append(resp).append('"').append(':').append('"').append(value).append('"');
        }
        json.append(" }");
        out.println(json);
    }
}
