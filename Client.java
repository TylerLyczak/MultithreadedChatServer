import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private ClientConnection clientConnection;

    public Client() {
        try {
            socket = new Socket("localhost", 5555);

            clientConnection = new ClientConnection(socket, this);
            clientConnection.start();

            // Thread for listening to input from the client
            listenForInput();
        }
        catch (UnknownHostException e)  {
            e.printStackTrace();
        }
        catch (IOException e)   {
            e.printStackTrace();
        }
    }

    // Used for waiting for the user to type input into the console and then send it to the server.
    public void listenForInput()    {
        Scanner console = new Scanner(System.in);

        Thread listen = new Thread( ()-> {
            while (true)    {
                while (!console.hasNextLine())  {
                    try {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)  {
                        e.printStackTrace();
                    }
                }

                String input = console.nextLine();

                if (input.toLowerCase().equals("quit")) {
                    SendObjectToServer("Client Close ID ");
                    break;
                }

                clientConnection.SendObjectToServer(input);
            }

            clientConnection.Close();
            Close();
        });
        listen.setName("ClientListenThread");
        listen.start();
    }

    // Sends a string to the server
    public void SendObjectToServer (String s)   {
        clientConnection.SendObjectToServer(s);
    }

    public void Close() {
        try {
            clientConnection.Close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        new Client();
    }
}
