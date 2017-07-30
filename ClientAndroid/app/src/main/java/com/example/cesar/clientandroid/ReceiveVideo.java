package com.example.cesar.clientandroid;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by cesarnassir on 2017-01-10.
 */

public class ReceiveVideo extends IntentService {

    DataInputStream dIn;
    ImageView imgView;
    Bitmap bitImage;
    private static final String debugString = "debug";


     public ReceiveVideo() {
        super(ReceiveVideo.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(debugString,"Intent service has started");

        Socket socket = SocketHandler.getSocket();
        byte[] message = null;
       // Bitmap bmp = null;

        while(true) {
            try {
                dIn = new DataInputStream(socket.getInputStream());

                int length = dIn.readInt();
                message = new byte[length];
                dIn.readFully(message);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeByteArray(message, 0, message.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            // broadcast the bitmap
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(DisplayImage.BReceiver.EXTRA_MESSAGE);
            broadcastIntent.putExtra("bitmapImage", byteArray);
            sendBroadcast(broadcastIntent);
        }

    }

}
