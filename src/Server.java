import jdk.dynalink.Operation;

import javax.management.OperationsException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

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
                Double result = doMath(msgFromClient);

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

    private static double doMath(String msgFromClient) {

        ArrayList<Integer> mathIndexes = new ArrayList<>();
        ArrayList<String> operations = new ArrayList<>();

        for (int i = 0; i < msgFromClient.length(); i++) {

            String currentChar = String.valueOf(msgFromClient.charAt(i));

            if (currentChar.equals("+") || currentChar.equals("-") || currentChar.equals("*") || currentChar.equals("/")) {
                mathIndexes.add(i);
                operations.add(currentChar);
            }
        }

        ArrayList<String> numbers = new ArrayList<>();
        int firstIndex = 0;

        for (int i = 0; i < mathIndexes.size(); i++) {
            numbers.add(msgFromClient.substring(firstIndex, mathIndexes.get(i)).trim());
            firstIndex = mathIndexes.get(i) + 1;
        }
        numbers.add(msgFromClient.substring(firstIndex).trim());

        double result = Double.parseDouble(numbers.get(0));

        int currentNumber = 1;

        for (String operation : operations) {
            double number = Double.parseDouble(numbers.get(currentNumber));

            switch (operation) {
                case "+":
                    result += number;
                    break;
                case "-":
                    result -= number;
                    break;
                case "*":
                    result *= number;
                    break;
                case "/":
                    if (number != 0) {
                        result /= number;
                    } else {
                        System.out.println("Cannot divdie by zero");
                        return Double.NaN;
                    }
                    break;
                default:
                    System.out.println("Impossible operator: " + operation);
                    return Double.NaN;
            }
            currentNumber++;
        }

        return result;
    }


}
