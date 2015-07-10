import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

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

			System.out.print("Message size: " + messageSize);
			System.out.print(byteBuffer.array());


			dataInputStream.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
}