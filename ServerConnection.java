import com.sun.xml.internal.bind.v2.model.core.ID;

import java.io.*;
import java.net.Socket;

public class ServerConnection extends Thread {

    private Socket socket;
    private Server server;
    private DataInputStream din;
    private DataOutputStream dout;
    private int clientID;
    private boolean isRun = true;

    // Constructor for the ServerConnection
    public ServerConnection (Socket socket, Server server, int clientID)  {
        super("ServerConnectionThread");
        this.socket = socket;
        this.server = server;
        this.clientID = clientID;
    }

    // Sends a given string to the client
    public void sendObjectToClient (String text)  {
        try {
            dout.writeUTF(text);
            dout.flush();
        }
        catch (IOException e)   {
            e.printStackTrace();
        }
    }

    public void sendObjectToAll (String text)    {
        for (int i=0; i<server.getConnections().size(); i++) {
            ServerConnection sc = server.getConnections().get(i);
            sc.sendObjectToClient(text);
        }
    }

    public int getClientID()    { return clientID;}

    public void closeSocket()   {
        try {
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()   {
        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Sends the new client that connected to the server its id
            sendObjectToClient("ClientID " + clientID);

            while (isRun)   {
                while (din.available() == 0)    {
                    try {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)   {
                        e.printStackTrace();
                    }
                }

                String textIn = din.readUTF();
                String[] split = textIn.split("\\s+");
                System.out.println(textIn);
                if (textIn.length() > 15)   {
                    if (split[0].equals("Client") && split[1].equals("Close"))  {
                        int clientCloseID = Integer.valueOf(split[3]);
                        server.RemoveClient(clientCloseID);
                        break;
                    }
                }
                sendObjectToAll(textIn);
            }

            din.close();
            dout.close();
            socket.close();
        }
        catch (Exception e)   {
            e.printStackTrace();
        }
    }
}
