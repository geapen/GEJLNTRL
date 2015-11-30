import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


	public class PrivateListener extends Thread{
		
		private int myPvtPort;
		private int myID;
		private TCPProxy myProxy; 
		private static boolean DEBUG_ON = false;
		private Boolean joinAckRcvd;
		private LamportMutex mutex;
		private DirectClock clock;
		
		public PrivateListener(int id, TCPProxy p,  Boolean joined, DirectClock clock, LamportMutex mutex) {

			this.myID = id;
			this.myProxy = p;
			this.myPvtPort = p.getMyPvtPort();
			this.joinAckRcvd = joined;
			this.clock = clock;
			this.mutex = mutex;
			this.DEBUG_ON = p.getDebugON();
		}
		
		public void run() {
			while (!Thread.interrupted()) {
				try {
					startProxyPvtListenerThread();
				} catch (Exception e) {
					System.out.println("Exception: " + e);
				}
			}
		}
		
		
		/*
		 * Starts ProxyPvtListenerThread after binding to private listening Port designated for TCPProxy
		 */
		private void startProxyPvtListenerThread() throws Exception {
						
			ServerSocket myPvtSocket = null;
			try {
				myPvtSocket = new ServerSocket(myPvtPort);
			}
			catch (IOException ie) {
				throw new IOException ("unable to bind to myPvtPort [" + myPvtPort + "]");
			}
			if (DEBUG_ON) {
				System.out.println("TCPProxy PvtListener started on port  [" + myPvtPort + "]");
				System.out.println("My PvtListener Socket Address is  [" + InetAddress.getLocalHost().getHostAddress() + ":" + myPvtPort + "]");
			}
			mutex.broadcastMessage("join " + myID + " " + clock.getValue(myID) + " " + clock.getValue(myID) + " " + "");
			
			while (true) {
				try {
					Socket proxyClient = myPvtSocket.accept();
					String clientAddress = proxyClient.getInetAddress().getHostAddress() + ":" + proxyClient.getPort();
					//Remove Debug Stmt
					/*
					if (DEBUG_ON) {
						System.out.println("Received message from Proxy instance at  [" + clientAddress + "]");
					}
					*/
					//ProxyPvtListenerThread(int id, TCPProxy p, Socket s, Boolean joined, LamportMutex mutex)
					ProxyPvtListenerThread proxylistener = new ProxyPvtListenerThread(myID, myProxy, proxyClient, joinAckRcvd, mutex);
					proxylistener.setDaemon(true);
					proxylistener.start();
				}
				catch (Exception e) {
					//Bubble Up after closing mySocket
					myPvtSocket.close();
					throw new Exception ("Unexpected error occured in ProxyPvtListener.\n" + e.toString());
				}
			}
			
		}
	}

