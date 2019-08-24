package Lesson_6.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;



    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connection();

            //String str = AuthService.getNickByLoginAndPass("login1","pass1");
            //System.out.println(str);
            //


            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }


    public void broadcastMsg(ClientHandler from, String msg, boolean forAll) {
        if(forAll) {
            for (ClientHandler o : clients) {
                if(!o.checkBlackList(from.getNickname()))
                o.sendMsg(msg);
            }
        }
    }

    public void whoIsOnline(){
        for(ClientHandler o: clients){
            System.out.println(o.getNickname());
            for (ClientHandler c: clients) {
                c.sendMsg(o.getNickname());
            }
        }
    }

    public void privateMassage(String msg, String nickTo, String nickFrom){
        for (ClientHandler o : clients) {
            if (nickTo.equals(o.getNickname())) {
                o.sendMsg(nickFrom + " >>: " + msg);
            }
            if (nickFrom.equals(o.getNickname())) {
                o.sendMsg(nickFrom + ": " + " >> " + nickTo + ": " + msg);
            }


        }
    }

    public boolean onlineCheck(String nick){
        boolean clientOnline = false;
        for(ClientHandler o: clients){
            if(nick.equals(o.getNickname())){
                clientOnline = true;
                break;
            }
        }
        return clientOnline;
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ClientHandler o : clients) {
            sb.append(o.getNickname() + " ");
        }

        String out = sb.toString();

        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}
