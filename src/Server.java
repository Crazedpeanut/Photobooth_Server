import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

public class Server
{
	public static void main(String[] args)
	{
		final String ipAddress = "0.0.0.0";
		final int port = 9999;
		
		boolean keepRunning = true;

		ServerSocket server = null;

		try
		{
			server = new ServerSocket(port);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		System.out.println("Starting Server");

		while(keepRunning)
		{
			Socket socket;

			try 
			{
				socket = server.accept();
				retrieveData(socket);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("Closing Server");	
		
		try
		{
			server.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void retrieveData(Socket socket)
	{
		DataInputStream dataInputStream;
		int messageSize;
		ByteBuffer byteBuffer;
		FilesSerializable filesSerializable;

		System.out.println("New Client Connection: " + socket.getInetAddress().toString() + ":" + socket.getPort());
		
		try
		{
			dataInputStream = new DataInputStream(socket.getInputStream());
			messageSize = dataInputStream.readInt();
			byteBuffer = ByteBuffer.allocate(messageSize);

			for(int i = 0; i < messageSize; i++)
			{
				byteBuffer.put(dataInputStream.readByte());
			}

			System.out.println(String.format("Message size: %d", messageSize));
			System.out.println(String.format("Byte buffer pos: %d", byteBuffer.position()));

			byteBuffer.position(0); //Put byte buffer pos back to the start

			System.out.println(String.format("Setting Byte buffer pos to: %d", byteBuffer.position()));

			filesSerializable = new FilesSerializable(byteBuffer);

			Random random = new Random();
			filesSerializable.saveFiles("\\images\\" + socket.getInetAddress().toString().replace(".", "") + random.nextInt());

			dataInputStream.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}