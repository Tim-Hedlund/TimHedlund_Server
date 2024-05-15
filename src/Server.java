import java.net.*;
import java.io.*;

public class Server {

    public static void main(String[] args) {

        ServerSocket server = null;
        Socket client;

        int portNumber = 55621; //för ports mellan 49152-65535 är dynamiska och har då mindre chans att förstöra för andra på nätverket

        if (args.length >= 1) {
            portNumber = Integer.parseInt(args[0]);
        }

        try {
            server = new ServerSocket(portNumber);
        } catch (IOException ie) {
            System.out.println("Cannot open socket." + ie);
            System.exit(1);
        }
        System.out.println("ServerSocket is created " + server);

        while (true) {

            try {

                System.out.println("Waiting for connect request...");
                client = server.accept();

                System.out.println("Connect request is accepted...");
                String clientHost = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                System.out.println("Client host = " + clientHost + ", Client port = " + clientPort);

                //read client data
                InputStream clientIn = client.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                String msgFromClient = br.readLine();

                System.out.println("Message recieved from client = " + msgFromClient);
                String result = doMath(msgFromClient);

                //send response to client
                if (msgFromClient != null && !msgFromClient.equalsIgnoreCase("bye")) {

                    OutputStream clientOut = client.getOutputStream();
                    PrintWriter pw = new PrintWriter(clientOut, true);
                    pw.println(result);
                }

                //close sockets
                if (msgFromClient != null && msgFromClient.equalsIgnoreCase("bye")) {

                    server.close();
                    client.close();
                    break;

                }

            } catch (IOException ie) {

                //tror det här är ett lämpligt meddelande
                System.out.println("Connection error: "+ ie.getMessage());

            }

        }

    }

    private static String doMath(String msgFromClient) {
        return msgFromClient;
    }

}
