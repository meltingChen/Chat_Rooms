import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


class WSResponse {
    private final OutputStream outputStream;

    public Room room;

    public static String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public String userName;

    public String userRoom;

    public WSResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(WSRequest request, Socket clientSocket) throws Exception {
        /* TODO
        if this is a websocket request:
            1. handshake
            2. keep handling messages
         else:
            reply http request
         */
        //
        //System.out.println(" SOCKET is: " + clientSocket.isClosed() + ", " + clientSocket.isConnected() );
        this.setStatus(101, "Switching Protocols");
        print("Upgrade: websocket\r\n");// + request.getHeaders().get("Upgrade") +"\r\n");
        print("Connection: Upgrade\r\n");// + request.getHeaders().get("Connection")+"\r\n");
        String secret = WSResponse.getWSResponseKey(request.headers.get("Sec-WebSocket-Key"));
        print("Sec-WebSocket-Accept: " + secret + "\r\n");
        print("\r\n"); // blank line - end of header
        this.outputStream.flush();
        while (true) {
            String[] pieces = this.getMsg(clientSocket);
            this.processMsg(pieces, clientSocket);
        }
    }

    public String[] getMsg(Socket clientSocket) throws Exception {
        // start reading WS message in binary
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        byte b0 = in.readByte();
        byte b1 = in.readByte();

        int opcode = b0 & 0x0F;
        int len = b1 & 0x7F; // 7 ==0111, F=1111, 7F=1111
        boolean isMasked = (b1 & 0x80) != 0;//8 0 ==1000 0000

        System.out.println("opcode:" + opcode + "len:" + len);

        if (!isMasked) {
            System.out.println("Error!");
            throw new Exception("unmasked msg from client");
        }

        byte[] mask = in.readNBytes(4);
        byte[] payload = in.readNBytes(len);

        byte[] DECODED = payload;

        //unmask the message
        for (var i = 0; i < payload.length; i++) {
            DECODED[i] = (byte) (payload[i] ^ mask[i % 4]);
        }
//        String msg = DECODED.toString();
        String msg = new String(DECODED);
//        String msg = new String(DECODED, StandardCharsets.UTF_8);


        System.out.println("Just got this message" + msg);

        String[] pieces = msg.split(" ");

        return pieces;
    }

    /*  Format
               '{
              "type"    : "message",
              "user"    : "theNameOfTheUserWhoSentTheMessage",
              "room"    : "nameOfRoom",
              "message" : "the message..."
                }'

               '{
              "type" : <join | leave>,
              "room" : "nameOfRoom",
              "user" : "theNameOfTheUserWhoSentTheMessage",
                }'
                                                                   */
    public void processMsg(String[] pieces, Socket clientSocket) throws IOException {
        Map<String, String> MapPayload = new HashMap<>();

        switch (pieces[0]) {
            case "join" -> {

                this.userName = pieces[1];
                this.userRoom = pieces[2];
                //TODO
                if (room == null || !room.roomName.equals(this.userRoom)) {
                    // ask the Room class to give you the room
                    this.room = Room.getRoom( this.userRoom );
                    //this.room = new Room(this.userRoom);
                }
                this.room.addUserToRoom(clientSocket, this.userName, this.userRoom);
                //this.sendMsg(MapPayload, clientSocket);
            }
            case "message" -> {

//                Room userRoom = room;
                this.room.broadcastMsgToRoom(clientSocket, this.userName, this.userRoom, pieces[1]);
//                ArrayList<Socket> allSockets = this.room.getSocketInRoom(this.userName);
//                for(Socket socket:allSockets) {
//                    this.sendMsg(MapPayload, socket);
//                }
            }
            case "leave" -> {

                this.room.removeUserFromRoom(clientSocket, this.userName, this.userRoom);

            }

        }
//        this.sendMsg(MapPayload, clientSocket);
    }

    public static void sendMsg(Map<String, String> MapPayload, Socket clientSocket) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
        dataOut.writeByte(0x81);// 1st byte of header:fin/ opcode => 1000 0001
        JSONObject jsonObject = new JSONObject(MapPayload);
        System.out.println(jsonObject);
        dataOut.writeByte(jsonObject.toString().length());// 2st byte of header:mask/ len => 0000len
        dataOut.writeBytes(jsonObject.toString());//send the payload
        dataOut.flush();
    }

    public static String getWSResponseKey(String requestKey) throws NoSuchAlgorithmException {
        //System.out.println("key: " + requestKey);
        String concatenatedString = requestKey + magicString;
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Results = sha1.digest(concatenatedString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sha1Results);
    }

    public void sendFile(String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            print("HTTP/1.1 200 OK\r\n");
            String type = "text/html";
            if (filePath.endsWith(".css")) {
                type = "text/css";
            }
            print("Content-Type: " + type + "\r\n");
            print("\r\n"); // blank line indicates end of header
            fileInputStream.transferTo(outputStream);
            File file = new File(filePath);
            int data;
            for (int i = fileInputStream.read(); i != -1; i = fileInputStream.read()) {
                outputStream.write(i);
                outputStream.flush();
                Thread.sleep(1); // Maybe add <- if images are still loading too quickly...
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void print(String content) throws IOException {
        System.out.print("about to send: " + content);
        outputStream.write(content.getBytes());
    }

    public void setStatus(int statusCode, String statusText) throws IOException {
        print("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
    }

    public void setContentType(String contentType) throws IOException {
        print("Content-Type: " + contentType + "\r\n");
    }

}