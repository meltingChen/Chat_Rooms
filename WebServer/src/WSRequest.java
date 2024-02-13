import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

class WSRequest {
    //    private String filePath;

    public HashMap<String, String> headers = new HashMap<>();

    private Socket socket;

    public String resourcesPath;

    public WSRequest(Socket socket) {
        this.socket = socket;
    }

    public void readHeadersFromSocket() throws IOException {
        InputStream inputStream = this.socket.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String requestLine1 = scanner.nextLine();
        String[] requestPart1 = requestLine1.split(" ");
        String fileName = requestPart1[1];
        //System.out.println(Arrays.toString(requestPart1));
        //System.out.println("file name: " + fileName + " " + fileName.length());

        if (fileName.equals("/")) {
            fileName = "/index.html";
        }
        this.resourcesPath = "resources" + fileName;

        //System.out.println("just set this.resourcesPath: " + this.resourcesPath);

        // TODO parse headers after the first line and store and key-value pair in a hashmap
        String requestLine = scanner.nextLine();
        while (!requestLine.isEmpty()) {

            //System.out.println(requestLine);
            String[] requestPart = requestLine.split(": ");
            String key = requestPart[0];
            String value = requestPart[1];
            headers.put(key, value);
            requestLine = scanner.nextLine();
        }
    }


    public static boolean isWSRequest(HashMap<String, String> headers) {
        System.out.println( "isWSRequest: " + headers.containsKey("Sec-WebSocket-Key"));
        return headers.containsKey("Sec-WebSocket-Key");// TODO change the logic
    }


    public void upgradeConnection() throws IOException {
        //send back the upgradeConnection's Response to the outputStream
        OutputStream outputStream = this.socket.getOutputStream();
    }

    public void listenAndHandleResponses() throws IOException {
        while (true) {
            InputStream inputStream = this.socket.getInputStream();
            inputStream.read();
        }
    }


//    public static void replyRequest(HTTPRequest request, HTTPResponse response) {
//        String filePath = request.getFilePath();
//        if (filePath.equals("/")) {
//            filePath = "/index.html";
//        }
//        String resourcesPath = ".//resources" + filePath;
//
//        try {
//            Path path = Paths.get(resourcesPath);
//            if (Files.exists(path) && !Files.isDirectory(path)) {
//                response.setStatus(200, "OK");
//                response.setContentType("text/html");
//                response.sendFile(resourcesPath);
//            } else {
//                response.setStatus(404, "Not Found");
//                response.setContentType("text/plain");
//                response.print("\r\n"); // blank line means end of headers
//                response.print("404 Not Found - The requested resource does not exist.");
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
}

//        String requestLine2 = scanner.nextLine();
//        String[] requestPart2 = requestLine2.split(": ");
//        otherRequest.put("Host", requestPart2[1]);
//        String requestLine3 = scanner.nextLine();
//        String[] requestPart3 = requestLine3.split(": ");
//        otherRequest.put("Upgrade", requestPart3[1]);
//        String requestLine4 = scanner.nextLine();
//        String[] requestPart4 = requestLine4.split(": ");
//        otherRequest.put("Connection", requestPart4[1]);
//        String requestLine5 = scanner.nextLine();
//        String[] requestPart5 = requestLine5.split(": ");
//        otherRequest.put("Sec-WebSocket-Key", requestPart5[1]);
//        String requestLine6 = scanner.nextLine();
//        String[] requestPart6 = requestLine6.split(": ");
//        otherRequest.put("Sec-WebSocket-Version", requestPart6[1]);