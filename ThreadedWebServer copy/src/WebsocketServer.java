import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class WebsocketServer {
    public static void main(String[] args) {
        int port = 8080;
        try {ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening for connection on port: " + port);

            while (true) {
                final Socket clientSocket;
                clientSocket = serverSocket.accept();
                //System.out.println("Waiting for client to connect...");
                Thread thread = new Thread(new connectionHandler(clientSocket));
                thread.start();
//                thread.join();
//                clientSocket.close();
                //System.out.println("closing...");
            }

        } catch (IOException  e) {
            System.out.println(e.getMessage());
        }
    }
}

