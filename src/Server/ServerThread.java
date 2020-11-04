package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ServerThread extends Thread{

    //protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    private Socket clientSocket = null;
    private String usernameClient = null;

    public ServerThread() throws IOException {
    }

    public ServerThread(Socket socket) throws IOException {
        super("ServerThread");
        this.clientSocket = socket;
    }

    public void run() {

        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ChatRoomProtocol chatRoomProtocol = new ChatRoomProtocol();

            boolean usernameAssigned = false;

            while (!usernameAssigned) {
                inputLine = in.readLine();
                usernameAssigned = chatRoomProtocol.setUsernameAndSocket(inputLine, clientSocket);

                if (usernameAssigned){
                    usernameClient = inputLine;
                    out.println("Username successfully assigned");

                    //Tell other people that you are online
                    HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();
                    System.out.println("Bezig met zenden van berichetne naar clients om te zeggen dat user online is");
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
                }
            }

            Set<String> onlineUsers = chatRoomProtocol.getOnlineUsers();
            Iterator i = onlineUsers.iterator();
            while (i.hasNext()) {

                out.println(i.next());
            }
            out.println("");

            while((inputLine = in.readLine()) != null){

                if(inputLine.equals("Send Message")){
                    String chatName = in.readLine();
                    if (chatName.equals("GroupChat")) {
                        String message = in.readLine();
                        HashMap<String, Socket> users = chatRoomProtocol.getOnlineUsersAndSockets();
                        System.out.println("Bezig met zenden van berichetne naar clients behalve naar de client zelf");
                        // Using for-each loop
                        for (Map.Entry mapElement : users.entrySet()) {
                            String user = (String)mapElement.getKey();
                            System.out.println(user);

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
                        System.out.println("sending private message to: " + chatName);

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
                    System.out.println("Bezig met zenden van berichetne naar clients om te zeggen dat user weg is");
                    // Using for-each loop
                    for (Map.Entry mapElement : users.entrySet()) {
                        String user = (String)mapElement.getKey();
                        if(!user.equals(usernameClient)){
                            Socket socketUser = (Socket)mapElement.getValue();
                            PrintWriter outUser = new PrintWriter(socketUser.getOutputStream(), true);
                            outUser.println("User is going Offline");
                            outUser.println(usernameClient);
                        }

                    }

                    break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
