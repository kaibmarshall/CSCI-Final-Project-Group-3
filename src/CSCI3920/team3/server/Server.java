package CSCI3920.team3.server;

import CSCI3920.team3.objects.Item;
import CSCI3920.team3.objects.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private final int port;
    private final int backlog;
    private int connectionCounter;
    private ServerSocket serverSocket;
    private boolean keepRunning;

    private ArrayList<User> users;
    private ArrayList<Item> items;

    public Server(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        this.connectionCounter = 0;
        this.keepRunning = true;
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public Server () {
        this(10001, 5);
    }

    public void populateListsForTesting() {
        users.add(new User("kaibmarshall", "password", true));
        users.add(new User("ahmed", "password", true));
        users.add(new User("kyung", "password", true));
        users.add(new User("user1", "password", false));
        users.add(new User("user2", "password", false));
        users.add(new User("user3", "password", false));

        items.add(new Item("Acer Laptop", 12.99));
        items.add(new Item("Samsung External SSD", 4.99));
        items.add(new Item("Macbook Pro", 12.99));
        items.add(new Item("Razer Mouse", 2.99));
        items.add(new Item("SomeBrand Keyboard", 2.99));
        items.add(new Item("SomeOther Item", 15.99));

    }

    public String attemptLogin(String username, String password) {
        String returnString = "Username or password is incorrect";
        for (User u : users){
            if (u.getUsername().equals(username) && u.getPassword().equals(password)){
                returnString = "Login Successful";
                break;
            }
        }
        return returnString;
    }

    private Socket waitForClientConnection() throws IOException {
        System.out.println("Waiting for connection...");
        Socket clientConnection = this.serverSocket.accept();
        this.connectionCounter++;
        System.out.printf("Connection #%d accepted from %s %n", this.connectionCounter, clientConnection.getInetAddress().getHostName());
        this.keepRunning = true;
        return clientConnection;
    }

    public void stop() {
        this.keepRunning = false;
    }

    public void run() {
        populateListsForTesting();
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
                this.serverSocket = new ServerSocket(this.port, this.backlog);

                while (keepRunning) {
                    try{
                        Socket clientConnection = this.waitForClientConnection();
                        ClientWorker cw = new ClientWorker(this, clientConnection, connectionCounter);

                        executorService.execute(cw);
                    }
                    catch (IOException ioe) {
                        System.out.println("\nServer Terminated---------------------------------");
                        ioe.printStackTrace();
                    }
                }

                try {
                    serverSocket.close();
                } catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
            catch (IOException ioe) {
                System.out.println("\n+++++++ Cannot open the server +++++++");
                executorService.shutdown();
                ioe.printStackTrace();
            }


    }

}
