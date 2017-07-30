package com.example.cesar.clientandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static String hostname = "your IP-address"; // your IP-address
    private static int port = 4444;
    private Socket socket = null;
    private DataOutputStream dOut = null;

    private static final String debugString = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread() {

            @Override
            public void run(){

            Log.i(debugString,"Attempting to connect to server");

                try

                {
                socket = new Socket(hostname, port);
                SocketHandler.setSocket(socket);

                Log.i(debugString, "Connection established");

                // receive message from the server
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.print("Message from the server: " + br.readLine());

                }

                catch(
                IOException e
                )
                {
                    Log.e(debugString, e.getMessage());
                }
            }

        }.start();

    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);

        try {
            // send message to server
            dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeByte(1);
            dOut.writeUTF(editText.getText().toString());
            dOut.flush(); // Send off the data

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showImage(View view) {
        Intent intent = new Intent(this, DisplayImage.class);
        startActivity(intent);
    }

    public Socket getSocket() {
        return socket;
    }

}
