package CSCI3920.team3.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication {
    public static void main (String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Server server = new Server(10001, 5);
        executorService.execute(server);
    }

}
