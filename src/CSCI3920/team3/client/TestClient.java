package CSCI3920.team3.client;

import CSCI3920.team3.objects.User;
import CSCI3920.team3.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestClient {

    private final int serverPort;
    private final String serverIp;
    private boolean isConnected;
    private Socket serverConnection;
    private PrintWriter output;
    private BufferedReader input;
    private String currentUserUsername;
    private String currentUserPassword;

    public TestClient(String ip, int port) {
        this.serverPort = port;
        this.serverIp = ip;
        this.isConnected = false;
    }

    public TestClient() {
        this("127.0.0.1", 10001);
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(this.serverConnection.getOutputStream(), true);
    }

    private BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new
                InputStreamReader(this.serverConnection.getInputStream()));
    }

    public void connect() {
        displayMessage("Attempting connection to Server");
        try {
            this.serverConnection = new Socket(this.serverIp, this.serverPort);
            this.output = this.getOutputStream();
            this.input = this.getInputStream();
            this.isConnected = true;  // at this point, no exception then we're connected!
            displayMessage("Connected");
        } catch (IOException e) { //If something went wrong....
            this.input = null;
            this.output = null;
            this.serverConnection = null;
            this.isConnected = false;
            displayMessage("Not Connected");
        }
    }

    public void disconnect() {
        displayMessage("Disconnecting From Server");
        try {
            this.input.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        try {
            this.output.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            this.serverConnection.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        displayMessage("Disconnected");
    }

    public String sendRequest(String request) throws IOException { //send message and returns the server response.
        displayMessage("Sending>> " + request);
        this.output.println(request);
        String srvResponse = this.input.readLine();
        displayMessage("Received>> " + srvResponse);
        return srvResponse;
    }

    private void displayMessage(String message) {  // We can improve this method to be log-type one
        System.out.println("[CLI]" + message);
    }

    /*
    private static void test() {
        int port = 10001;
        Server aServer = new Server(port, 100);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(aServer);
        try { Thread.sleep(500);} //giving time for the server to  start
        catch (InterruptedException ignored) { }
        TestClient c = new TestClient("127.0.0.1", port);

        c.connect();

        try {
            c.currentUserUsername = "kaibmarshall";
            c.currentUserPassword = "password";
            c.displayMessage("Attempting to login as " + c.currentUserUsername);
            c.sendRequest("login|" + c.currentUserUsername + "|" + c.currentUserPassword);

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        c.disconnect();
        try { Thread.sleep(500);}
        catch (InterruptedException ignored) { }
        executorService.shutdown();
        try { executorService.awaitTermination(1_000, TimeUnit.MILLISECONDS);}
        catch (InterruptedException ignored) {}
        executorService.shutdownNow();
    }

    public static void main(String[] args) {

        test();
        System.out.println("Main Ended");
    }
    */
}

