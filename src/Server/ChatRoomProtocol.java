package Server;

import java.net.Socket;
import java.util.*;

public class ChatRoomProtocol {

    private String username;
    private Socket userSocket;
    private static HashMap<String, Socket> onlineUsers = new HashMap<>();

    public ChatRoomProtocol () {
    }

    public boolean checkIfUsernameIsTaken (String username) {
        // Check of username bestaat
        boolean usernameTaken = onlineUsers.containsKey(username);
        return usernameTaken;
    }

    public String getUsername() {
        return username;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public Set<String> getOnlineUsers(){
        Set onlineUsersList = onlineUsers.keySet();
        return onlineUsersList;
    }

    public HashMap<String,Socket> getOnlineUsersAndSockets(){
        return onlineUsers;
    }

    //Methode synchronized maken zodat zeker geen twee dezelfde usernames kunnen gebruikt worden
    public synchronized boolean setUsernameAndSocket(String username, Socket userSocket) {
        boolean succes = false;

        if(!checkIfUsernameIsTaken(username)){
            succes = true;
            this.username = username;
            this.userSocket =userSocket;
            onlineUsers.put(username, userSocket);
        }
        return succes;
    }

    public void removeUser(String username){
        onlineUsers.remove(username);
    }
}
