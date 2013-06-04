import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class EchoServer
{
	public static void main (String[] args)
		throws IOException
	{
		EchoServer ws = new EchoServer();
		
		System.out.println ("WebServer listening on 127.0.0.1:80.");
		System.out.println ("Type Ctrl-C to shutdown");
		
		ws.listen();
	}
	
	private class ShutdownHook
		extends Thread
	{
		public void run ()
		{
			shutdown = 1;
			
			try
			{
				System.out.println ("Closing listen socket ...");
				listenSocket.close();
				serverThread.join();
			}
			catch (InterruptedException e)
			{
				System.err.println ( e.getMessage() );
			}
			catch (IOException e)
			{
				System.err.println (e.getMessage());
			}
		}
	}
	
	private class Worker
		implements Runnable
	{
		private Socket socket;
		
		public Worker (Socket s)
		{
			socket = s;
		}
		
		public void run ()
		{
			try (BufferedReader in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
				PrintWriter out = new PrintWriter (socket.getOutputStream(), true)
			)
			{
				out.println ("Server: This is a simple echo server! Send your message and it will be echoed back");
				
				String line;
				while ( (line = in.readLine()) != null )
				{
					String[] bits = line.split(":");
					
					System.out.println ("Read " + line.getBytes().length + " bytes from client");
					System.out.println ("Client said: " + line);
					
					out.println ("Server: " + bits[1]);
				}
				
				System.out.println ("Server: Goodbye");
				System.out.println ("Closing client ...");
				
				socket.close();
			}
			catch (IOException e)
			{
				System.err.println ("IO Error: " + e.getMessage());
			}
		}
	}
	
	private final Thread serverThread = Thread.currentThread();
	
	private ServerSocket listenSocket;
	private volatile int shutdown;
	
	public EchoServer()
		throws IOException
	{
		listenSocket = new ServerSocket(80);
		
		Runtime.getRuntime().addShutdownHook (new ShutdownHook());
	}
	
	public void listen()
		throws IOException
	{
		while ( shutdown == 0 )
		{
			System.out.println ("Waiting for connection ...");
			
			Socket socket = listenSocket.accept();
			
			System.out.format ("Connection from: %s:%d -> %s:%d\n", socket.getInetAddress().getHostAddress(), socket.getPort(), socket.getLocalAddress().getHostAddress(), socket.getLocalPort());
			
			new Thread(new Worker(socket)).start();
		}
	}
}