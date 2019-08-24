package Lesson_6.client;

import Lesson_6.server.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Controller {

    @FXML
    ListView<HBox> listView;


    @FXML
    TextField textField;

    @FXML
    Button btn1;

    @FXML
    HBox bottomPanel;

    @FXML
    HBox upperPanel;

    @FXML
    HBox upperPanel2;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientList;

    @FXML
    TextField loginFieldReg;

    @FXML
    TextField nicknameFieldReg;

    @FXML
    PasswordField passwordFieldReg;


    private boolean isAuthorized;
    private boolean registration = false;


    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;

        if(!isAuthorized){
            upperPanel2.setVisible(true);
            upperPanel2.setManaged(true);
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        }
        else {
            upperPanel2.setVisible(false);
            upperPanel2.setManaged(false);
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    String myNick;

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok")) {
                                setAuthorized(true);
                                String[] tokens = str.split(" ");
                                myNick = tokens[1];
                                break;
                            } else {
                                Platform.runLater(
                                        () -> {
                                            getMassage(str, "Server: ");
                                        }
                                );
                            }
                        }

                        while (true) {
                            String nick;
                            String str = in.readUTF();
                            String[] nickGetter = str.split(": ");
                            nick = nickGetter[0];
                            if (str.equals("/serverclosed")) break;
                            if (str.startsWith("/clientlist")) {
                                String[] tokens = str.split(" ");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clientList.getItems().clear();
                                        for (int i = 1; i < tokens.length; i++) {
                                            clientList.getItems().add(tokens[i]);
                                        }
                                    }
                                });
                            } else {
                                Platform.runLater(
                                        () -> {
                                            getMassage(str, nick);
                                        }
                                );
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setAuthorized(false);
                    }
                }
            });
            t1.setDaemon(true);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Dispose() {
        System.out.println("Отправляем сообщение о закрытии");
        try {
            if(out != null) {
                out.writeUTF("/end");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectClient(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            System.out.println("Двойной клик");
        }
    }

    public void getMassage(String str, String nick){

            HBox hBox = new HBox();
            Label label = new Label(str + "\n");
            hBox.getChildren().add(label);

        if(nick.equals(myNick)){
            hBox.setAlignment(Pos.TOP_RIGHT);
            label.setAlignment(Pos.TOP_RIGHT);
        }else {
            hBox.setAlignment(Pos.TOP_LEFT);
            label.setAlignment(Pos.TOP_LEFT);
        }

            listView.getItems().add(hBox);

    }


    public void toRegister(ActionEvent actionEvent) {
        registration = true;
        if((socket==null)||socket.isClosed()){
            connect();
        }

        try {
            out.writeUTF("/register " + loginFieldReg.getText() + " " + nicknameFieldReg.getText() + " " + passwordFieldReg.getText());
            loginFieldReg.clear();
            passwordFieldReg.clear();
            nicknameFieldReg.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//    @FXML
//    private void closeButtonAction(){
//        // get a handle to the stage
//        Stage stage = (Stage) closeButton.getScene().getWindow();
//        out.writeUTF("/close")
//        // do what you have to do
//        stage.close();
//    }
}
