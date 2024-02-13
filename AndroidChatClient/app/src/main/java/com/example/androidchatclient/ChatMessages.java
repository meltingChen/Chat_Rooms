package com.example.androidchatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatMessages extends AppCompatActivity {


    public static ListView messages;

    protected static ArrayAdapter<String> messagesAdapter;
    public TextView roomTxtView;
    private String uname;
    private String rname;

    public static ArrayList<String> messageList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);
        messages = findViewById(R.id.chatBox);
        roomTxtView = findViewById(R.id.roomTxt);
        messageList = new ArrayList<>();
        //Adapter helps the ListView display the messages
        messagesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, (List) messageList);
        messages.setAdapter(messagesAdapter);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // The key argument below must match that used in the other activity
            uname = extras.getString("USERNAME");
            rname = extras.getString("ROOMNAME");
            roomTxtView.setText(rname);
        }




    }
    public void sendMsg(View v){

        if(v.getId() == R.id.sendBtn){
            Log.d("send", "send Button Clicked");
        EditText msgEditText = findViewById(R.id.msgTxt);
        loginPage.ws.sendText("message "+msgEditText.getText().toString());
        msgEditText.setText("");}
    }
    public void leaveAction(View v){
        Log.d("leave", "leave Button Clicked");
        loginPage.ws.sendText("leave");
        //go back to the login page
        Intent intent = new Intent(this, loginPage.class);
        startActivity(intent);

    }

}
