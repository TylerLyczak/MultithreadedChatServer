public class ServerClientChat {

    public static void main (String[] args) {
        boolean isServer = false;
        Server server;
        Client client;

        if (isServer)   {
            server = new Server();
        }
        else    {
            client = new Client();
        }
    }
}
