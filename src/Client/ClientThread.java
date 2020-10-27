package Client;

import javafx.application.Application;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket serverSocket = null;
    private String username = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private ClientController controller;

    public ClientThread() {
    }

    public ClientThread(Socket socket, PrintWriter out, BufferedReader in, String username, ClientController controller) {
        super("ClientThread");
        this.serverSocket = socket;
        this.out = out;
        this.in = in;
        this.username = username;
        this.controller = controller;
    }

    public void run() {

        try {

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                if(inputLine.equals("New Message")){
                    String chatName = in.readLine();
                    String message = in.readLine();
                    Platform.runLater(new Runnable () {
                        @Override
                        public void run() {
                            controller.addMessageToChatRoom(chatName, message);
                        }
                    });

                }
                else if(inputLine.equals("User is going Offline")){

                    String name = in.readLine();
                    Platform.runLater(new Runnable () {
                        @Override
                        public void run() {

                            controller.removeOnlineUser(name);
                        }
                    });

                }
                else if(inputLine.equals("New Online User")){
                    String name = in.readLine();
                    Platform.runLater(new Runnable () {
                        @Override
                        public void run() {

                            controller.addOnlineUser(name);
                        }
                    });
                }

            }


        } catch (IOException e) {
                e.printStackTrace();
            }

    }
}