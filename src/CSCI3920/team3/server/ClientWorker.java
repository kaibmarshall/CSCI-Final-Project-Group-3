package CSCI3920.team3.server;

import CSCI3920.team3.objects.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker implements Runnable{
    private final Server server;
    private final Socket clientConnection;
    private int clientNum;

    boolean keepRunning;
    private BufferedReader input;
    private PrintWriter output;


    public ClientWorker(Server server, Socket connection, int clientNum) {
        this.clientNum = clientNum;
        this.server = server;
        this.clientConnection = connection;
        this.keepRunning = true;
    }

    private void getOutputStream(Socket clientConnection) throws IOException {
        this.output = new PrintWriter(clientConnection.getOutputStream(), true);
    }

    private void getInputStream(Socket clientConnection) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
    }

    private void processClientRequest(String msg) throws IOException {
        String[] args = msg.split("\\|");
            switch(args[0]) {
                case "login":
                    sendMessage(server.attemptLogin(args[1], args[2]));
                    break;
                default:
                    sendMessage("Unsupported function.");
                    break;
            }

    }

    private void displayMessage (String msg) {
        System.out.println("[SERVER] " + msg);
    }

    private void closeClientConnection(Socket clientConnection, BufferedReader input, PrintWriter output) {
        try {input.close();} catch (IOException|NullPointerException e) {e.printStackTrace();}
        try {output.close();} catch (NullPointerException e) {e.printStackTrace();}
        try {clientConnection.close();} catch (IOException|NullPointerException e) {e.printStackTrace();}

    }

    private void sendMessage(String message) {
        this.output.println(message);
    }


    @Override
    public void run() {
        try {
            getOutputStream(clientConnection);
            getInputStream(clientConnection);

            while (this.keepRunning) {
                try {
                    String clientMessage = input.readLine();
                    processClientRequest(clientMessage);
                }
                catch (Exception e) {
                    displayMessage("Client terminated.("+e.getMessage()+")");
                    this.keepRunning = false;
                    closeClientConnection(this.clientConnection, this.input, this.output);
                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            closeClientConnection(clientConnection, input, output);
        }
    }
}
