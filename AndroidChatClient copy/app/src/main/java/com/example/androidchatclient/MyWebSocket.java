package com.example.androidchatclient;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MyWebSocket extends WebSocketAdapter {


    MyWebSocket() {
        super();
//        userName_ = userName;
//        roomName_ = roomName;
    }

    @Override
    public void onTextMessage(WebSocket websocket, String message) throws Exception {
/*      let msgJSON = JSON.parse(msg);
        uname = msgJSON.user;
        uroom = msgJSON.room;
        console.log(msgJSON);

        if (msgJSON.type === "join") {
            let ppl = document.createElement("p");
            let newText = document.createElement("p");
            newText.textContent = uname + " joins " + uroom;
            ppl.textContent = uname + " is in the room";
            yellowBox.appendChild(ppl);
            redBox.appendChild(newText);

        } else if (msgJSON.type === "leave") {
            let newText = document.createElement("p");
            let pplText = document.createElement("p");
            newText.textContent = uname + " leaves " + uroom;
            pplText.textContent = uname + " left";
            yellowBox.appendChild(pplText);
            redBox.appendChild(newText);
        } else if (msgJSON.type === "message") {
            let newText = document.createElement("p");
            newText.textContent = uname + ": " + msgJSON.message;
            redBox.appendChild(newText);
        }*/
        JSONObject msgJSON = new JSONObject(message);
        String newMsg = new String();
        if(msgJSON.getString("type").equals("join")){
            newMsg = msgJSON.getString("user")+" joins"+msgJSON.getString("room");
        } else if (msgJSON.getString("type").equals("message")) {
            newMsg = msgJSON.getString("user")+": "+msgJSON.getString("message");
        } else if (msgJSON.getString("type").equals("leave")) {
            newMsg = msgJSON.getString("user")+" leaves"+msgJSON.getString("room");
        }
        ChatMessages.messageList.add(newMsg);

        ChatMessages.messages.post(new Runnable() {
            @Override
            public void run() {
                ChatMessages.messagesAdapter.notifyDataSetChanged();
                ChatMessages.messages.smoothScrollToPosition(ChatMessages.messagesAdapter.getCount());
            }
        });

    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        Log.d("MyWebSocket", "Web Socket Connected!");
    }
    @Override
    public void onConnectError(WebSocket webSocket, WebSocketException exception) {
        Log.d("MyWebSocket", "Socket Connect Failed!");
    }

    @Override
    public void onError(WebSocket webSocket, WebSocketException cause) {
        Log.d("MyWebSocket", "An error occurred");
    }





}
