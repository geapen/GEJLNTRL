import java.net.*; import java.io.*; import java.util.*;
public class TCPListener extends Thread{

	JokeRepo repo;
	int tcpPort;
	
	public TCPListener(JokeRepo repo, int tcpPort) {
		// TODO Auto-generated constructor stub
		
		this.repo = repo;
		this.tcpPort = tcpPort;
	}
	
	public void run() {

		TCPServerThread t;
		try {
			ServerSocket listener = new ServerSocket(tcpPort);
			Socket clientSocket;  
		
			while ( (clientSocket = listener.accept()) != null) {
				t = new TCPServerThread(repo, clientSocket);
				t.start();
			}
	  
		} catch (IOException e) {
			System.err.println("Server aborted:" + e);
		}

	}

}