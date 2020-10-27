package Client;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        //loader.setLocation(getClass().getResource("ChatRoomGUI.fxml"));
        Parent root = (Parent)loader.load();

        primaryStage.setTitle("Chat Room");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        ClientController controller = loader.getController();

        String hostName = "localhost";
        int portNumber = 4500;


        Socket socket = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

        controller.setSocket(socket);
        controller.setPrintWriter(out);
        controller.setBufferReader(in);

            //Username creation and check
            boolean usernameChosen = false;
            String dialogHeader = "Choose your username";

            while(!usernameChosen) {
                TextInputDialog dialog = new TextInputDialog();

                // inlezen van de usernaam
                dialog.initOwner(primaryStage);
                dialog.setTitle("Welcome");
                dialog.setHeaderText(dialogHeader);
                dialog.setContentText("Username");
                dialog.setGraphic(null);
                String userName = null;
                try {
                    userName = dialog.showAndWait().get();
                } catch (Exception e) {
                }

                if(userName != null && !userName.trim().equals("")){
                    out.println(userName);
                    String answer = in.readLine();
                    if(answer.equals("Username successfully assigned")) {
                        usernameChosen = true;
                        primaryStage.setTitle(userName + "'s Chat Room");
                    }
                    else if(answer.equals("Username already exist, please choose an other username!")){
                        dialogHeader = answer;
                    }
                }
            }


            //receiving online users, after last received online users "" is send
            OnlineUsersList onlineUsersList = new OnlineUsersList();

            controller.setOnlineUsersList(onlineUsersList);

            onlineUsersList.addObserver(controller);

            controller.updateOnlineUsers();





    }


    public static void main(String[] args) {
        launch(args);
    }

}
