package server;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

public class Receiver extends Thread {

	private static int captureWidth = 480;// 1280;
	private static int captureHeight = 320;// 720;
	private static long DELAY = 200;
	private static long duration;

	private static OpenCVFrameGrabber grabber;
	private static Frame capturedFrame = null;
	private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();
	
	// test
	private Socket clientSocket;
	
	Receiver(Socket s) {
		this.clientSocket = s;
	}

	public void run() {

		try {
			System.out.println("Connection from : " + clientSocket.getInetAddress().getHostAddress() + ':'
					+ clientSocket.getPort());

			// send message to client
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			bw.write("This is a message from the server");
			bw.newLine();
			bw.flush();

			// to receive message from the client
			DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());

			// to send message/Image to client
			DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

			boolean done = false;
			byte messageType;
			while (!done) {

				try {
					messageType = dIn.readByte();

				} catch (EOFException ex1) {
					break; // break out from loop if no message or app closed
				}

				switch (messageType) {
				case 1: // Type A
					System.out.println("Message A: " + dIn.readUTF());
					break;

				case 2: // Type B
					System.out.println("Message B: " + dIn.readUTF());

					// start sending frames from camera
					startGrab();
					try {
						while ((capturedFrame = grabber.grab()) != null) {
							
							// if klient pauses break loop (frame grabbing)
							if((dIn.available() > 0) && (dIn.readBoolean() == true)) {
								grabber.flush();
								grabber.stop();
								break;
							}
							
							long startTime = System.currentTimeMillis();

							Java2DFrameConverter c = new Java2DFrameConverter();
							OpenCVFrameConverter.ToIplImage c2 = new OpenCVFrameConverter.ToIplImage();

							BufferedImage buffImg = frameConverter.convert(capturedFrame);

							capturedFrame = c.convert(buffImg);
							FaceDetection idf = new FaceDetection(c2.convert(capturedFrame));
							buffImg = c.convert(c2.convert(idf.originalImg));

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(buffImg, "jpg", baos);
							baos.flush();

							byte[] bytes = baos.toByteArray();
							baos.close();

							dOut.writeInt(bytes.length);
							dOut.write(bytes, 0, bytes.length);
							
							duration = System.currentTimeMillis() - startTime;
							
							// to avoid frame drops and overloading the system
							if (duration < DELAY) {
								try {
									Thread.sleep(DELAY - duration);
								} catch (InterruptedException ex) {
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				default:
				done = true;
				}
			}

			bw.close();
			dIn.close();
			dOut.close();
			System.out.println("Client ended session");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void startGrab() {
		// start grabber
		grabber = new OpenCVFrameGrabber(0);
		grabber.setImageWidth(captureWidth);
		grabber.setImageHeight(captureHeight);
		try {
			grabber.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
