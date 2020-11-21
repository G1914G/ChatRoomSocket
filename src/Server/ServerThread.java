//Written by Glenn Groothuis
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ServerThread extends Thread{

    private Socket clientSocket = null;
    private String usernameClient = null;

    public ServerThread() throws IOException {
    }

    public ServerThread(Socket socket) throws IOException {
        super("ServerThread");
        System.out.println("Creating server thread, because client has connected.");
        this.clientSocket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
        ) {
            String inputLine;
            ChatRoomProtocol chatRoomProtocol = new ChatRoomProtocol();

            boolean usernameAssigned = false;

            //In the setup a username needs to be assigned to the user
            while (!usernameAssigned) {
                System.out.println("Assigning a username and checking if username is already taken.");
                inputLine = in.readLine();
                usernameAssigned = chatRoomProtocol.setUsernameAndSocket(inputLine, clientSocket);

                if (usernameAssigned){

                    usernameClient = inputLine;
                    out.println("Username successfully assigned");
                    System.out.println("Username successfully assigned");

                    //Tell other people that you are online
                    HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();
                    System.out.println("Busy with sending update messages to clients to say that a new client is online...");

                    // Using for-each loop
                    for (Map.Entry mapElement : users.entrySet()) {
                        String user = (String)mapElement.getKey();
                        if(!user.equals(usernameClient)){
                            Socket socketUser = (Socket)mapElement.getValue();
                            PrintWriter outUser = new PrintWriter(socketUser.getOutputStream(), true);
                            outUser.println("New Online User");
                            outUser.println(usernameClient);
                        }

                    }

                }
                else{
                    out.println("Username already exist, please choose an other username!");
                    System.out.println("Username already exist, username was not successfully assigned.");
                }
            }

            System.out.println("Sending to the new user all the currently online users.");
            Set<String> onlineUsers = chatRoomProtocol.getOnlineUsers();
            Iterator i = onlineUsers.iterator();
            while (i.hasNext()) {
                out.println(i.next());
            }
            out.println("");
            System.out.println("Sending info about all the online users is done.");

            System.out.println("Start listening to incoming messages from the client.");
            while((inputLine = in.readLine()) != null){

                if(inputLine.equals("Send Message")){

                    String chatName = in.readLine();

                    if (chatName.equals("GroupChat")) {

                        String message = in.readLine();
                        HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();
                        System.out.println("New message arrived from "+usernameClient+": "+message);
                        System.out.println("Busy with sending message to all users in the Groupchat...");

                        for (Map.Entry mapElement : users.entrySet()) {
                            String user = (String)mapElement.getKey();
                            System.out.println("Sending message to "+user);

                            if(!user.equals(usernameClient)){
                                Socket socketUser = (Socket)mapElement.getValue();
                                PrintWriter outUser = new PrintWriter(socketUser.getOutputStream(), true);
                                outUser.println("New Message");
                                outUser.println("GroupChat");
                                outUser.println(usernameClient+": "+message);
                            }

                        }
                    }
                    else{
                        String message = in.readLine();
                        HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();
                        System.out.println("Sending private message from "+usernameClient+" to " + chatName);

                        Socket socketUser = (Socket)users.get(chatName);
                        PrintWriter outUser = new PrintWriter(socketUser.getOutputStream(), true);
                        outUser.println("New Message");
                        outUser.println(usernameClient);
                        outUser.println(usernameClient+": "+message);


                    }
                }
                else if (inputLine.equals("Bye")) {

                    chatRoomProtocol.removeUser(in.readLine());

                    HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();

                    System.out.println("Busy sending messages to all online users to say that "+usernameClient+" is going offline...");
                    for (Map.Entry mapElement : users.entrySet()) {
                        String user = (String)mapElement.getKey();
                        if(!user.equals(usernameClient)){
                            System.out.println("Sending message to "+user+", to say that "+usernameClient+" is going offline.");
                            Socket socketUser = (Socket)mapElement.getValue();
                            PrintWriter outUser = new PrintWriter(socketUser.getOutputStream(), true);
                            outUser.println("User is going Offline");
                            outUser.println(usernameClient);
                        }

                    }

                    break;
                }
            }

            System.out.println("Closing connection with client: "+usernameClient);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
