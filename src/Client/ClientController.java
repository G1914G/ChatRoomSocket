package Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ClientController implements Observer {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private OnlineUsersList onlineUsersList;

    @FXML
    private ListView<String> onlineUsers;

    private ObservableList<String> items = FXCollections.observableArrayList();

    public ClientController() {
    }

    public void setOnlineUsersList(OnlineUsersList onlineUsers){
        this.onlineUsersList = onlineUsers;
        System.out.println("Toekennen");
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public void setPrintWriter(PrintWriter out){
        this.out = out;
    }

    public void setBufferReader(BufferedReader in){
        this.in = in;
    }

    public void updateOnlineUsers() throws IOException {
        List<String> onlineUsers = new ArrayList<String>();
        String onlineUser;
        while ((onlineUser = in.readLine()) != null){
            onlineUsers.add(onlineUser);
            System.out.println(onlineUser);
        }

        onlineUsersList.setOnlineUsers(onlineUsers);
        System.out.println("Hier");
    }

    @FXML
    public void update(Observable arg0, Object arg1) { // Called from the Model
        System.out.println("Update gedetecteerd");
        System.out.println(onlineUsersList.getOnlineUsers());
        onlineUsers.setItems(items);
        List<String> users = onlineUsersList.getOnlineUsers();
        for(int i=0;i<users.size();i++){
            items.add(users.get(i));
        }

    }
}
