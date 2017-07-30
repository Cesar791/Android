package com.example.cesar.clientandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DisplayImage extends AppCompatActivity {

    private static final String debugString = "debug";

    ImageView imageView;
    ReceiveVideo img;
    private  BReceiver receiver;
    DataOutputStream dOut;
    Socket socket;
    IntentFilter filter;

    private boolean STOP_SENDING_FRAMES = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        socket = SocketHandler.getSocket();

        DataInputStream dIn;
        Intent intent = getIntent();

        // create Image view and add it to window
        imageView = (ImageView) findViewById(R.id.imageView1);

        Log.i(debugString, "New activity started");

        try {
            // send message to server
            dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeByte(2);
            dOut.writeUTF("STARTING VIDEO");
            dOut.flush(); // Send off the data

            Log.i(debugString, " 'Camera' messsage sent");

            // receive message from server
            dIn = new DataInputStream(socket.getInputStream());
;
            // register broadcast receiver
            filter = new IntentFilter(BReceiver.EXTRA_MESSAGE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new BReceiver();
            registerReceiver(receiver, filter);

            // start intent service
            startService(new Intent(getBaseContext(), ReceiveVideo.class));

            Log.i(debugString, "Outside the service");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(debugString, "Image received");

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);

        try {
            dOut.writeBoolean(STOP_SENDING_FRAMES);
            dOut.flush(); // Send off the data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class BReceiver extends BroadcastReceiver {
        public final static String EXTRA_MESSAGE = "com.example.cesarnassir.networkapp.MESSAGE";

        public BReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(debugString, "broadcast received");

            ImageView result = (ImageView) findViewById(R.id.imageView1);

            byte[] bytes = intent.getByteArrayExtra("bitmapImage");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            result.setImageBitmap(bitmap);
            Log.i(debugString, "bitmap updated");
        }
    }
}
