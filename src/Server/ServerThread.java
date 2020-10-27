package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServerThread extends Thread{

    //protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    private Socket clientSocket = null;

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
                    out.println("Username successfully assigned");
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

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
