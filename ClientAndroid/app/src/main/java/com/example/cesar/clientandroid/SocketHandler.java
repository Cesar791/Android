package com.example.cesar.clientandroid;

import java.net.Socket;

/**
 * Created by cesarnassir on 2017-01-10.
 */

public class SocketHandler {
    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}
