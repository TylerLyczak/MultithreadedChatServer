import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Thread {

    private Socket socket;
    private Client client;
    private DataInputStream din;
    private DataOutputStream dout;
    private int clientID;
    private boolean isRun = true;

    public ClientConnection (Socket socket, Client client)  {
        this.socket = socket;
        this.client = client;
    }

    // Sends whatever string is given to the class
    public void SendObjectToServer (String text)    {
        try {
            dout.writeUTF(text);
            dout.flush();
        }
        catch (IOException e)   {
            e.printStackTrace();
            Close();
        }
    }

    @Override
    public void run() {

        try {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Sends a welcome message to the server and other clients
            SendObjectToServer("Client Connected to Server");

            while (isRun) {
                try {
                    while (din.available() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // This is where the client will read replays from the server
                    String reply = din.readUTF();
                    System.out.println(reply);
                    String[] split = reply.split("\\s+");

                    if (reply.equals("Server Closed"))  {
                        client.Close();
                    }
                    else if (reply.length() > 5)    {
                        if (split[0].equals("ClientID"))    {
                            clientID = Integer.valueOf(split[1]);
                        }
                    }

                }
                catch (IOException e)   {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)   {
            e.printStackTrace();
            Close();
        }

    }

    public void Close() {
        try {
            din.close();
            dout.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
