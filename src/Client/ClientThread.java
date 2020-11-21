//Written by Glenn Groothuis
package Client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private BufferedReader in = null;
    private ClientController controller;

    public ClientThread() {
    }

    public ClientThread(BufferedReader in, ClientController controller) {
        super("ClientThread");
        this.in = in;
        this.controller = controller;
    }

    public void run() {

        try {

            String inputLine;

            while ((inputLine = in.readLine()) != null) {

                if(inputLine.equals("New Message")){
                    String chatName = in.readLine();
                    String message = in.readLine();
                    //To access GUI from a non application thread
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