import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;

public class Server {

    private ServerSocket serverSocket;
    private ArrayList<ServerConnection> connections;
    private ArrayList<Integer> clientNums;
    private int clientIDIncrement;
    private boolean isRun = true;

    // Constructor for the server
    public Server () {
        try {
            serverSocket = new ServerSocket(5555);
            connections = new ArrayList<>();
            clientNums = new ArrayList<>();
            clientIDIncrement = 0;

            Thread socketListener = new Thread( () ->   {
                while (isRun)   {
                    try {
                        Socket socket = serverSocket.accept();
                        ServerConnection serverConnection = new ServerConnection(socket, this, clientIDIncrement);
                        clientNums.add(clientIDIncrement);
                        serverConnection.start();
                        connections.add(serverConnection);
                        //serverConnection.sendObjectToClient("ClientID " + clientIDIncrement);
                        clientIDIncrement++;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socketListener.setName("SocketListenerThread");
            socketListener.start();

            // Starts the listener thread so the server can communicate with the clients
            ListenForInput();
        }
        catch (IOException e)   {
            e.printStackTrace();
            closeServer();
        }
    }

    // Listens for input for the server's console to send to all clients
    public void ListenForInput () {
        Scanner console = new Scanner(System.in);
        Thread serverListener = new Thread( () -> {
            while (true) {
                // Thread sleeps when there is no input
                while (!console.hasNextLine()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String input = console.nextLine();
                System.out.println(input);
                System.out.println("\n");
                for (int i = 0; i < connections.size(); i++) {
                    connections.get(i).sendObjectToClient(input);
                }
            }
        });
        serverListener.setName("InputListenThread");
        serverListener.start();
    }

    public void SendObjectToAll (String s)  {
        for (int i=0; i<connections.size(); i++)    {
            connections.get(i).sendObjectToClient(s);
        }
    }

    public void RemoveClient (int index)    {
        for (int i=0; i<connections.size(); i++)    {
            if (connections.get(i).getClientID() == index)  {
                connections.get(i).closeSocket();
                connections.remove(i);
                break;
            }
        }
    }

    // Closes server and all of its connections
    public void closeServer() {
        try {
            serverSocket.close();
            isRun = false;
            for (int i=0; i<connections.size(); i++)    {
                connections.get(i).sendObjectToClient("Server Closed");
                connections.get(i).closeSocket();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ServerConnection> getConnections ()    { return connections;}

    public ArrayList<Integer> getClientNums ()              { return clientNums;}

    public static void main (String[] args)  {
        new Server();
    }
}
