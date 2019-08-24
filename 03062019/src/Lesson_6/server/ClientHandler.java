package Lesson_6.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nick;
    private Vector<String> blacklist;


    public boolean checkBlackList(String nick) {
        blacklist = AuthService.getBlackList(this.nick);
        return blacklist.contains(nick);
    }

    public ClientHandler(Server server, Socket socket) {
        blacklist = new Vector<>();
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                                if (newNick != null) {
                                    if(!server.onlineCheck(newNick)) {
                                        sendMsg("/authok "+ newNick);
                                        nick = newNick;
                                        server.subscribe(ClientHandler.this);
                                        break;
                                    }else{
                                        sendMsg("Server: Такой пользователь уже в сети");
                                    }
                                } else {
                                    sendMsg("Server: Неверный логин/пароль!");
                                }
                            }
                            if (str.startsWith("/register")) {
                                String[] tokens = str.split(" ");
                                String log = tokens[1];
                                String nick = tokens[2];
                                String pass = tokens[3];
                                if(AuthService.registerCheck(log,nick)){
                                    AuthService.registerNewClient(log, nick, pass);
                                    sendMsg("Server: Вы успешно зарегистрированы!\n Можете авторизоваться.");
                                }else
                                    sendMsg("Server: Пользователь с таким логином \n или ником уже существует!");

                            }
                        }

                        while (true) {
                            boolean forAll = true;
                            String str = in.readUTF();
                            System.out.println(nick + ": " + str);

                            if (str.equals("/end")) {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            if (str.startsWith("/w")) {
                                forAll = false;
                                if(!str.equals("/w") && !str.equals("/w ")) {
                                    String[] pmTokens = str.split(" ");
                                    String nickTo = pmTokens[1];
                                    if (server.onlineCheck(nickTo)) {
                                        server.privateMassage(personalMsgBuilder(pmTokens), nickTo, nick);
                                    } else {
                                        sendMsg("Server: Такого пользователья нет в сети");
                                    }
                                }else
                                    sendMsg("Server: Задайте имя");

                            }
                            if (str.startsWith("/newNick")) {
                                forAll = false;
                                if(!str.equals("/newNick") && !str.equals("/newNick ")) {
                                    String[] newNickTokens = str.split(" ");
                                    String newNick = newNickTokens[1];
                                    AuthService.setNickname(nick, newNick, ClientHandler.this);
                                }else
                                    sendMsg("Server: Вы не задали новый ник");

                            }
                            if(str.startsWith("/checkbl")){
                                forAll = false;
                                blacklist = AuthService.getBlackList(nick);
                                for(String o: blacklist){
                                    sendMsg("Server: "+ o);
                                }
                            }
                            if (str.startsWith("/blacklist ")) {
                                forAll = false;
                                String[] tokens = str.split(" ");
                                if(checkBlackList(tokens[1])) {
                                    sendMsg("Server: Такой пользователь уже есть в вашем чёрном списке");
                                }
                                else{
                                    AuthService.addToBlackList(tokens[1], nick);
                                    sendMsg("Server: Вы добавили пользователя " + tokens[1] + " в черный список");
                                }
                            }
                            if (str.startsWith("/online")) {
                                forAll = false;
                                server.whoIsOnline();
                            }
                            server.broadcastMsg(ClientHandler.this, nick + ": " + str, forAll);


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                        System.out.println(nick + " отключился от сервера");
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname(){
        return nick;
    }

    public String personalMsgBuilder(String[] pmTokens){

        String msg = "";
        for(int i = 2; i<pmTokens.length; i++){
            msg += pmTokens[i]+ " ";
        }
        return msg;
    }
}
