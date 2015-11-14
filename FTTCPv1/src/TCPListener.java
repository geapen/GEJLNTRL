import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/*
 * TCPSListener handles the clients connecting to TCPProxy. 
 * It determines an online server (usually the 1st one in list),
 * initiates a connection, and begins forwarding client traffic
 * to that server.
 * When forwarding fails or the threads are stopped, the sockets
 * are closed.
 */

public class TCPListener {

	private static final boolean DEBUG_ON = true;
	private TCPProxy myProxy = null;
	private ServerTarget targetServer = null;
	private boolean client_server_Online = false;
	private String clientAddress = "hostname:port";
	private String serverAddress = "hostname:port";	
	private Socket tcpClient = null;
	private Socket tcpServer = null;

	/*
	 * Create a client thread and pass client socket here
	 */
	public TCPListener (TCPProxy p, Socket s) {
		this.myProxy = p;
		this.tcpClient = s;
	}
	
	public void run() {
		try {
			clientAddress = tcpClient.getInetAddress().getHostAddress() + ":" + tcpClient.getPort();
			tcpServer = openServerSocket();
			if (tcpServer == null) {
				if (DEBUG_ON) {
					System.out.println("[Servers OFFLINE] Cannot open connection for client " + clientAddress); 
				}
				try {
					tcpClient.close();
				}
				catch (IOException ie) {
					if (DEBUG_ON) {
						ie.printStackTrace();
					}
				}
				//Exit
				return;
			}
			
			serverAddress = tcpServer.getInetAddress().getHostAddress() + ":" + tcpServer.getPort();
			
			InputStream clientIn = tcpClient.getInputStream();
			OutputStream serverOut = tcpServer.getOutputStream();
			InputStream serverIn = tcpServer.getInputStream();
			OutputStream clientOut = tcpClient.getOutputStream();
			
			// Perfect Point to Inject Logger perhaps
			if (DEBUG_ON) {
				System.out.println("Forwarded " + clientAddress + " to " + serverAddress); 
			}
			
			TCPStreamFwdThread clientProxy = new TCPStreamFwdThread (this, clientIn, serverOut);
			
		}
		catch (IOException ie){
			ie.printStackTrace();
		}
	}
	
	private ServerTarget getServerTarget() {
		ServerTarget target = null;
		
		return target;
	}
	
	/*
	 * Use synchronized here to not deadlock
	 */
	public synchronized void tcpConnectionClosed() {
		//decide what to do here
	}
	private Socket openServerSocket() throws IOException {
		while (true) {
			targetServer = getServerTarget();
			if (targetServer == null) {
				return null;
			}
			try {
				Socket s = new Socket(targetServer.getHostName(),targetServer.getPort());
				targetServer.incrementClientCount();
				return s;
			}
			catch (IOException ie) {
				targetServer.setOffline();
			}
		}
		
	}
}
