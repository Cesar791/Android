package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketImpl;

public class Server {

	public static ServerSocket serverSocket;
	public static SocketImpl port;
	private static final int portnumber = 4444;

	public static void main(String[] args) throws IOException {
		
		try {
			System.out.println("Server start at port number: " + portnumber);
			serverSocket = new ServerSocket(portnumber);
			while (true) {
				// client connecting
				System.out.println("Waiting for client to connect");
				Receiver r = new Receiver(serverSocket.accept());
				r.start();
			}

		} catch (IOException e) {
			System.exit(1);
		}

	}

}
