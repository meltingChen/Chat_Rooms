package com.example.androidchatclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;


public class loginPage extends AppCompatActivity {
    protected static WebSocket ws;
    private String uName;
    private String rName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        //joinBt = findViewById(R.id.joinBtn);
        try {
            ws = new WebSocketFactory().createSocket("ws://10.0.2.2:8080/endpoint");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ws.addListener(new MyWebSocket());
        ws.connectAsynchronously();
    }
    //Button joinBt;

    public void passInfo(View v){
        EditText userName = findViewById(R.id.userName);
        EditText roomName = findViewById(R.id.roomName);
        Intent intent = new Intent(this, ChatMessages.class);
        uName = userName.getText().toString();
        rName = roomName.getText().toString();
        intent.putExtra("USERNAME",uName);
        intent.putExtra("ROOMNAME", rName);
        startActivity( intent );
        ws.sendText("join " + uName + " " + rName);
    }

}
