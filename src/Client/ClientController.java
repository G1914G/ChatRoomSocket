//Written by Glenn Groothuis
package Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ClientController implements Observer {

    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private OnlineUsersList onlineUsersList;
    private List<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
    private String currentActiveChatRoom = "GroupChat";

    @FXML
    public TextArea chatBox;

    @FXML
    public Button sendMessageButton;

    @FXML
    public TextField messageInput;

    @FXML
    private ListView<String> onlineUsers;

    private ObservableList<String> items = FXCollections.observableArrayList();

    public ClientController() {
    }

    public void setOnlineUsersList(OnlineUsersList onlineUsers){
        this.onlineUsersList = onlineUsers;
    }

    public void setPrintWriter(PrintWriter out){
        this.out = out;
    }

    public void setBufferReader(BufferedReader in){
        this.in = in;
    }

    public void updateOnlineUsers() throws IOException {
        List<String> onlineUsers = new ArrayList<String>();
        onlineUsers.add("GroupChat");
        String onlineUser;
        while (!(onlineUser = in.readLine()).equals("")){
            onlineUsers.add(onlineUser);
        }

        onlineUsersList.setOnlineUsers(onlineUsers);

    }

    @FXML
    public void update(Observable arg0, Object arg1) { // Called from the Model
        onlineUsers.getItems().clear();
        onlineUsers.setItems(items);
        List<String> users = onlineUsersList.getOnlineUsers();
        for(int i=0;i<users.size();i++){
            items.add(users.get(i));
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageInput.getText();
        this.addMessageToChatRoom(currentActiveChatRoom, username+": "+message);
        if(!currentActiveChatRoom.equals(username)){
            out.println("Send Message");
            out.println(currentActiveChatRoom);
            out.println(message);
        }
        messageInput.clear();
    }

    public void deleteMyUsername() {
        out.println("Bye");
        System.out.println(username+" is closing the connection.");
        out.println(username);
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public void addChatRoom(ChatRoom chatroom) {
        chatRooms.add(chatroom);
    }

    public void addMessageToChatRoom(String chatName, String message) {

        //Find chat with appropriate name
        boolean chatroomFound = false;
        ChatRoom chatroom = null;
        int i=0;
        while(!chatroomFound&&chatRooms.size()!=i){
            //Add message to correct chatroom
            if(chatRooms.get(i).getChatRoomName().equals(chatName)){
                chatroom = chatRooms.get(i);
                chatroomFound = true;
                chatroom.addMessage(message);
                System.out.println(message);

            }
            i++;
        }

        //If chatroom doesn't exists yet -> make one and add message
        if(!chatroomFound) {
            chatroom = new ChatRoom(chatName);
            this.addChatRoom(chatroom);
            chatroom.addMessage(message);
        }

        //If current active chatroom is the chatroom of the new message, then update view
        if(chatName.equals(currentActiveChatRoom)){
            if(currentActiveChatRoom.equals("GroupChat")&&chatBox.getText().equals("Welcome to the group chat")){
                chatBox.appendText("\n");
            }
            chatBox.appendText(message + "\n");
            int caretPosition = chatBox.caretPositionProperty().get();
            chatBox.positionCaret(caretPosition);
        }

    }

    public void removeOnlineUser(String name) {
        onlineUsersList.removeOnlineUser(name);

        //If current active chatroom equals the user that is going offline
        //-> change view to Groupchat and change current active chatroom
        if(currentActiveChatRoom.equals(name)){

            ChatRoom currentChatRoom = new ChatRoom();

            boolean chatRoomExists = false;
            int i=0;
            while(!chatRoomExists&&i!=chatRooms.size()){
                if(chatRooms.get(i).getChatRoomName().equals(name)){
                    chatRoomExists = true;
                    chatRooms.remove(i);
                }
                i++;
            }

            currentActiveChatRoom = "GroupChat";

            chatRoomExists = false;
            i=0;
            while(!chatRoomExists&&i!=chatRooms.size()){
                if(chatRooms.get(i).getChatRoomName().equals(currentActiveChatRoom)){
                    chatRoomExists = true;
                    currentChatRoom = chatRooms.get(i);
                }
                i++;
            }

            chatBox.setText(currentChatRoom.toString());
        } else {
            boolean chatRoomExists = false;
            int i=0;
            while(!chatRoomExists&&i!=chatRooms.size()){
                if(chatRooms.get(i).getChatRoomName().equals(name)){
                    chatRoomExists = true;
                    chatRooms.remove(i);
                }
                i++;
            }
        }
    }

    public void addOnlineUser(String name) {
        onlineUsersList.addOnlineUser(name);
    }

    public void createPrivateChat(MouseEvent mouseEvent) {
        String name = onlineUsers.getSelectionModel().getSelectedItem();

        ChatRoom currentChatRoom = new ChatRoom();

        //Check if chatroom doesnt already exists
        boolean chatRoomExists = false;
        int i=0;
        while(!chatRoomExists&&i!=chatRooms.size()){
            if(chatRooms.get(i).getChatRoomName().equals(name)){
                chatRoomExists = true;
                currentChatRoom = chatRooms.get(i);
            }
            i++;
        }

        if(!chatRoomExists){
            currentChatRoom = new ChatRoom(name);
            this.addChatRoom(currentChatRoom);
        }

        //Change view
        currentActiveChatRoom = name;
        chatBox.setText(currentChatRoom.toString());
    }
}