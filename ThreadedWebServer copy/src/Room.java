import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/*
Add persistent storage for messages. Write the messages that are sent to a room onto disk.
When the server starts up, read those messages (probably one file per room) in.
Note, you may need a separate "meta" file name "roomNames.txt" that just stores the names of the rooms.
*/
public class Room {
    public String roomName;
    public HashMap<String, Socket> clients;
    public LinkedHashMap<String, String> historyMsg = new LinkedHashMap<>();
    public static HashMap<String, Room> rooms = new HashMap<>();

    public static Room getRoom(String roomname) {
        // if name exists return that room (from rooms hashmap)
        Room room = rooms.get(roomname);
        if (room == null) {
            room = new Room(roomname);
            rooms.put(roomname, room);
        }
        // otherwise create a new room with "name"
        return room;
    }

    private Room(String roomName) {
        this.roomName = roomName;
        this.clients = new HashMap<>();
    }


    public synchronized void addUserToRoom(Socket clientsocket, String userName, String roomName) throws IOException {
//        if(rooms.containsKey(roomName)){
//            this.clients.add(clientsocket);
//            this.rooms.put(roomName,userName);
//        }
        // Let the new client know about everyone that is already in the room.
        Map<String, String> MapPayload = new HashMap<>();
        MapPayload.put("type", "join");
        MapPayload.put("room", roomName);
        //send it
        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            MapPayload.put("user", entry.getKey());
            WSResponse.sendMsg(MapPayload, clientsocket);
        }


        if (historyMsg != null) {
            // Let the new client know about history message that is already in the room.
            Map<String, String> MapPayload2 = new HashMap<>();
            MapPayload2.put("type", "message");
            MapPayload2.put("room", roomName);
            //send
            for (Map.Entry<String, String> entry2 : historyMsg.entrySet()) {
                MapPayload2.put("user", entry2.getKey());
                MapPayload2.put("message", entry2.getValue());
                WSResponse.sendMsg(MapPayload2, clientsocket);
            }
        }

        // Now add the new client to the room
        this.clients.put(userName, clientsocket);

        MapPayload.put("user", userName);
        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            Socket socket = entry.getValue();
            WSResponse.sendMsg(MapPayload, socket);
        }


    }

    public synchronized void broadcastMsgToRoom(Socket clientSocket, String userName, String userRoom, String msg) throws IOException {
        // Let the new client know about everyone that is already in the room.
        Map<String, String> MapPayload = new HashMap<>();
        MapPayload.put("type", "message");
        MapPayload.put("room", roomName);
        MapPayload.put("user", userName);
        MapPayload.put("message", msg);
        historyMsg.put(userName, msg);

        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            Socket socket = entry.getValue();
            WSResponse.sendMsg(MapPayload, socket);
        }
    }


    public synchronized void removeUserFromRoom(Socket clientSocket, String userName, String userRoom) throws IOException {
        // Let the new client know about everyone that is already in the room.
        Map<String, String> MapPayload = new HashMap<>();
        MapPayload.put("type", "leave");
        MapPayload.put("room", userRoom);
        MapPayload.put("user", userName);

        clients.get(userName).close();
        clients.remove(userName);

        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
            Socket socket = entry.getValue();
            WSResponse.sendMsg(MapPayload, socket);
        }

//        this.rooms.values().remove(userName);
//        this.clients.remove(userName);
    }


}

//    public ArrayList<Socket> getSocketInRoom(String userName) {
//        // Let the new client know about everyone that is already in the room.
//        Map<String, String> MapPayload = new HashMap<>();
//        MapPayload.put("type", "join");
//        MapPayload.put("room", roomName);
//        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
//            MapPayload.put("user", entry.getKey());
//            WSResponse.sendMsg( MapPayload, clientsocket );
//        }
//
//        // Now add the new client to the room
//        this.clients.put(userName,clientsocket);
//
//        MapPayload.put("user", userName);
//        for (Map.Entry<String, Socket> entry : clients.entrySet()) {
//            Socket socket = entry.getValue();
//            WSResponse.sendMsg( MapPayload, socket );
//        }
//
//        ArrayList<Socket> socketsInRoom = new ArrayList<>();
//        for (String name : clients.keySet()) {
//            if (roomName.equals(this.roomName)) {
//                socketsInRoom.add(clients.get(name));
//            }
//        }
//        return socketsInRoom;
//    }
