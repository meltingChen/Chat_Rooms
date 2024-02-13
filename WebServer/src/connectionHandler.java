import java.net.Socket;
import java.util.ArrayList;

public class connectionHandler implements Runnable {
    public final Socket clientSocket;
    public Room room;
    public ArrayList<Room> rooms;

    public String test;

    connectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {

            WSRequest request = new WSRequest(this.clientSocket);
            //HashMap<String, String> headers = request.getHeaders();
            request.readHeadersFromSocket();

            if (WSRequest.isWSRequest(request.headers)) {
                System.out.println("Go into WebSocket Mode...");

                //response by outputStream
                WSResponse wsResponse = new WSResponse(clientSocket.getOutputStream());
                wsResponse.sendResponse(request, clientSocket);

            } else {
                WSResponse wsHttpResponse = new WSResponse(clientSocket.getOutputStream());
                System.out.println("request.resourcesPath: " + request.resourcesPath);
                wsHttpResponse.sendFile(request.resourcesPath);
                clientSocket.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

