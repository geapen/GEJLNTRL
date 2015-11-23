import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


/*
 * TCPSListener handles the clients connecting to TCPProxy. 
 * It determines an online server (usually the 1st one in list),
 * initiates a connection, and begins forwarding client traffic
 * to that server.
 * When forwarding fails or the threads are stopped, the sockets
 * are closed.
 */

public class TCPListenerThread extends Thread{

	private static boolean DEBUG_ON = false;
	
	private TCPProxy myProxy = null;
	private ServerTarget targetServer = null;
	private boolean client_server_Online = false;
	private String clientAddress = "hostname:port";
	private String serverAddress = "hostname:port";	
	private Socket tcpClient = null;
	private Socket tcpServer = null;
	TCPStreamFwdThread clientProxy;
	TCPStreamFwdThread serverProxy;

	/*
	 * Create a client thread and pass client socket here
	 */
	public TCPListenerThread (TCPProxy p, Socket s) {
		this.myProxy = p;
		this.tcpClient = s;
		this.DEBUG_ON = p.getDebugON();
	}
	
	/*
	 * Opens destination Server Socket and starts two threads for TCP Stream forwarding
	 * from Proxy Standpoint as Follows:
	 * 		- clientIn to TargetServerOut
	 * 		- TargetServerIn to ClientOut
	 * Listener waits until one of the threads aborts due to connection closure or other failure.
	 * Listener then closes the other open endpoint.
	 */
	public void run() {
		
		try {
			clientAddress = tcpClient.getInetAddress().getHostAddress() + ":" + tcpClient.getPort();
			tcpServer = openServerSocket();
			if (tcpServer == null) {
				//if tcpServer is NULL, all targets are offline
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
			
			//From Proxy Standpoint, maps as Follows:
			//	 * 		- clientIn to TargetServerOut
			//	 * 		- TargetServerIn to ClientOut
			
			InputStream clientIn = tcpClient.getInputStream();
			OutputStream serverOut = tcpServer.getOutputStream();
			InputStream serverIn = tcpServer.getInputStream();
			OutputStream clientOut = tcpClient.getOutputStream();
			
			// Perfect Point to Inject our Logger perhaps
			if (DEBUG_ON) {
				System.out.println("Forwarded " + clientAddress + " to " + serverAddress); 
			}
			
			//Start TCP Stream forwarding and keep track of proxy references
			clientProxy = new TCPStreamFwdThread (this, clientIn, serverOut, StreamSource.CLIENT);
			serverProxy = new TCPStreamFwdThread (this, serverIn, clientOut, StreamSource.SERVER);
			
			client_server_Online = true;
			clientProxy.start();
			serverProxy.start();
			
			//DO I really need to join??
/*			
		    try {
		        //u.join();
		    	clientProxy.join();
		    	serverProxy.join();
		    } catch (Exception e) { 
				if (DEBUG_ON) {
					System.out.println("There was an exception in proxy threads join: "); 
				}
		        e.printStackTrace();
		    }
*/			
		}
		catch (IOException ie){
			client_server_Online = false;
			if (DEBUG_ON) {
				ie.printStackTrace();
			}
		}

	}
	
	// returns 1st ONLINE server from List
	private ServerTarget getServerTarget() {
		ServerTarget target = null;
		ArrayList<ServerTarget> TargetList = myProxy.getServerTargetList();
		Iterator<ServerTarget> it = TargetList.iterator();
		while (it.hasNext()) {
			target = it.next();
			if (target.isOnline()) {
				//return 1st online Server
				return target;
			}
		}
		// Return null if no online server found
		return null;
	}
	
	/*
	 * Use synchronized here to not deadlock
	 */
	public synchronized void tcpConnectionClosed(StreamSource s) {
		//decide what to do here
		if (client_server_Online) {
			//If Server died, failover to next available server and start new stream proxy
			if (s == StreamSource.SERVER) {
			
				//tcpServer.close();  Verify that Server did die with SendUrgentData in isSocketRemoteEndOpen()
				if (DEBUG_ON) {
					System.out.println("Did Server die?" ); 
				}
				if (!isSocketRemoteEndOpen(tcpServer)) {
					//Server died!  Initiate Failover.
					try {
						//decrement client count
						targetServer.decrementClientCount();
						tcpServer.close();
						
						// Another Point to Inject our Logger perhaps
						if (DEBUG_ON) {
							System.out.println("ServerTarget died. Need to transparently redirect client"); 
						}
						
						//open connection to next available server
						//same code as in run
						tcpServer = openServerSocket();
						if (tcpServer == null) {
							if (DEBUG_ON) {
								System.out.println("[Servers OFFLINE] Cannot open failover connection for client " + clientAddress); 
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
							//return;
						}
						//update serverAddress value
						serverAddress = tcpServer.getInetAddress().getHostAddress() + ":" + tcpServer.getPort();

						OutputStream serverOut2 = tcpServer.getOutputStream();
						InputStream serverIn2 = tcpServer.getInputStream();
						OutputStream clientOut2 = tcpClient.getOutputStream();
						
						
						//Update clientProxy with new Server outputstream
						clientProxy.setOutputStream(serverOut2);
						
						//create new ServerTarget proxy Thread
						serverProxy = new TCPStreamFwdThread (this, serverIn2, clientOut2, StreamSource.SERVER);
						serverProxy.start();
						if (DEBUG_ON) {
							System.out.println("Re-Forwarded " + clientAddress + " to " + serverAddress); 
						}						
					}
					catch (IOException ie) {
						client_server_Online = false;
					}
				
				}
				else {
					if (DEBUG_ON) {
						System.out.println("Server did not die.." ); 
					}
				}

			}
			
			
			if (s == StreamSource.CLIENT) {
				//Client Close.  Just clean up connections to server

				if (DEBUG_ON) {
					System.out.println("Client terminated connection.  Clean-up Server connection(s)" ); 
				}	
				
				try {
					tcpServer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				if (DEBUG_ON) {
					System.out.println("TCP Stream forwarding stopped between" + clientAddress + " and " + serverAddress ); 
				}
				targetServer.decrementClientCount();
				client_server_Online = false;
					
			}
				
		}
	}
	
	/*
	 * Opens a connection to an ONLINE TargetServer.  If a server is detected as OFFLINE, tag server accordingly
	 * If one targetServer is ONLINE, a connection is re-established (after a small delay) to that server
	 * but the Client is unaware of the Transition.  From Client's perspective, it is Fault Tolerant.  
	 */
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
	
	private boolean isSocketRemoteEndOpen(Socket s) {
		boolean result = true;
		
		try {
			//explicitly verify that server's socket is closed
			s.sendUrgentData(0);
		} catch (IOException ie) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			result = false;
		}
		return result;
	}
}
