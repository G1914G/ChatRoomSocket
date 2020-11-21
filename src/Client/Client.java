//Written by Glenn Groothuis
package Client;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Setting up GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        Parent root = (Parent)loader.load();

        primaryStage.setTitle("Chat Room");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        ClientController controller = loader.getController();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                controller.deleteMyUsername();
            }
        });


        String hostName = "localhost";
        int portNumber = 4500;

        //Making connection with server
        Socket socket = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

        controller.setPrintWriter(out);
        controller.setBufferReader(in);

        //Username creation and check
        boolean usernameChosen = false;
        String dialogHeader = "Choose your username";

        String userName = null;

        while(!usernameChosen) {
            TextInputDialog dialog = new TextInputDialog();

            // Reading input for username
            dialog.initOwner(primaryStage);
            dialog.setTitle("Welcome");
            dialog.setHeaderText(dialogHeader);
            dialog.setContentText("Username");
            dialog.setGraphic(null);

            try {
                userName = dialog.showAndWait().get();
            } catch (Exception e) {
            }

            //Some checks on client side and after that on server side
            if(userName != null && !userName.trim().equals("")){
                out.println(userName);
                String answer = in.readLine();
                if(answer.equals("Username successfully assigned")) {
                    usernameChosen = true;
                    controller.setUsername(userName);
                    primaryStage.setTitle(userName + "'s Chat Room");
                }
                else if(answer.equals("Username already exist, please choose an other username!")){
                    dialogHeader = answer;
                }
            }
        }


        //receiving online users for setup, after last received online users "" is send
        OnlineUsersList onlineUsersList = new OnlineUsersList();

        controller.setOnlineUsersList(onlineUsersList);

        onlineUsersList.addObserver(controller);

        controller.updateOnlineUsers();

        //Creating default chatroom -> GroupChat
        ChatRoom chatroom = new ChatRoom("GroupChat");
        controller.addChatRoom(chatroom);

        //Start client thread that listens to updates and messages from the server
        new ClientThread(in, controller).start();

    }


    public static void main(String[] args) {
        launch(args);
    }

}