import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Properties;

public class TCPProxy {
	private static boolean DEBUG_ON = false;
	public static final String PROPERTIES_FILE = "config.properties";
	private static int RCV_BUF = 8192;
	private int myPort;
	private int myPvtPort;
	//private int numProxy;
	private int myID;
	private boolean listenerActive = false;
	private Boolean joinAckReceived = Boolean.FALSE;
	//http://javahungry.blogspot.com/2015/03/difference-between-array-and-arraylist-in-java-example.html
	//private ServerTarget[] arServerTargets = null;
	private ArrayList<ServerTarget> serverTargetList;
	private ArrayList<ServerTarget> proxyTargetList;
	private long pollIntervalMs;
	private LamportMutex mutex;
	private DirectClock clock;

	public long getPollIntervalMs() {
		return this.pollIntervalMs;
	}
	
	public int getMyPort() {
		return myPort;
	}
	
	public int getMyPvtPort() {
		return myPvtPort;
	}
	
	public int getMyID() {
		return myID;
	}
	/*
	public int getNumProxyInstances() {
		return numProxy;
	}
	*/
	
	public boolean getDebugON() {
		return DEBUG_ON;
	}
	
	public boolean isListenerActive () {
		return listenerActive;
	}
	public ArrayList<ServerTarget> getServerTargetList() {
		return this.serverTargetList;
	}
	
	public ArrayList<ServerTarget> getProxyTargetList() {
		return this.proxyTargetList;
	}
	
	//http://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
	public void ReadMyConfig(String id) throws IOException{
		InputStream inputStream = null;
		try {
			

			this.myID = Integer.parseInt(id) - 1;
			
			Properties props = new Properties();
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			this.DEBUG_ON = Boolean.parseBoolean(props.getProperty("debugON"));
			// get the property value and print it out
			String serverTargets = props.getProperty("serverTargets");
			this.myPort = Integer.parseInt(props.getProperty("myPort"));
			if (DEBUG_ON) {
				System.out.println("my Port: " + this.myPort); 
			}
			this.pollIntervalMs = Long.parseLong(props.getProperty("pollIntervalMs"));
			if (DEBUG_ON) {
				System.out.println("Poll Interval: " + this.pollIntervalMs); 
			}
			this.RCV_BUF = Integer.parseInt(props.getProperty("receiveBuffer"));
			if (DEBUG_ON) {
				System.out.println("Receive Buffer: " + this.RCV_BUF); 
			}
			String proxyTargets = props.getProperty("proxyTargets");
			
			serverTargetList = new ArrayList<ServerTarget>();
			StringTokenizer stServers = new StringTokenizer(serverTargets,",");
			int i = 1;
			while (stServers.hasMoreTokens()) {
				String serverPort = stServers.nextToken();
				if (DEBUG_ON) {
					System.out.println("ServerTarget #" + i + ": " + serverPort);
				}
				ServerTarget target = new ServerTarget();
				target.setAddress(serverPort);
				serverTargetList.add(target);
				i++;
			}

			proxyTargetList = new ArrayList<ServerTarget>();
			StringTokenizer stProxies = new StringTokenizer(proxyTargets,",");
			i = 0;
			while (stProxies.hasMoreTokens()) {
				i++;
				String proxyHostnamePort = stProxies.nextToken();
				if (DEBUG_ON) {
					System.out.println("TCPProxy #" + i + ": " + proxyHostnamePort);
				}
				ServerTarget target = new ServerTarget();
				target.setAddress(proxyHostnamePort);
				proxyTargetList.add(target);
			}
			//since can extract from size, no need to store explicitly
			//this.numProxy = proxyTargetList.size();
			this.myPvtPort = proxyTargetList.get(myID).getPort();

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
	}
	
	/*
	 * Starts ProxyPvtListenerThread after binding to private listening Port designated for TCPProxy
	 */
	public void startProxyPvtListenerThread() throws Exception {
		this.clock = new DirectClock(proxyTargetList.size(), myID);
		this.mutex = new LamportMutex(myID, clock, proxyTargetList, joinAckReceived);
		mutex.broadcastMessage("join " + myID + " " + clock.getValue(myID) + " " + clock.getValue(myID) + " " + "");
		
		ServerSocket myPvtSocket = null;
		try {
			myPvtSocket = new ServerSocket(myPvtPort);
		}
		catch (IOException ie) {
			throw new IOException ("unable to bind to myPvtPort [" + myPvtPort + "]");
		}
		if (DEBUG_ON) {
			System.out.println("TCPProxy PvtListener started on port  [" + myPvtPort + "]");
			System.out.println("My Socket Address is  [" + InetAddress.getLocalHost().getHostAddress() + ":" + myPvtPort + "]");
		}
		
		while (true) {
			try {
				Socket proxyClient = myPvtSocket.accept();
				String clientAddress = proxyClient.getInetAddress().getHostAddress() + ":" + proxyClient.getPort();
				if (DEBUG_ON) {
					System.out.println("Received message from Proxy instance at  [" + clientAddress + "]");
				}
				//ProxyPvtListenerThread(int id, TCPProxy p, Socket s, Boolean joined, LamportMutex mutex)
				ProxyPvtListenerThread proxylistener = new ProxyPvtListenerThread(myID, this, proxyClient, joinAckReceived, mutex);
				proxylistener.start();
			}
			catch (Exception e) {
				//Bubble Up after closing mySocket
				myPvtSocket.close();
				throw new Exception ("Unexpected error occured in ProxyPvtListener.\n" + e.toString());
			}
		}
		
	}
	
	/*
	 * Starts ListenerThread after binding to listening Port designated for TCPProxy
	 */
	public void startTCPListenerThread() throws Exception {
		//bind to myPort
		//block here with Lamport?
		if (DEBUG_ON) {
			System.out.println("Proxy [" + myID + "] trying to acquire Lamport Mutex");
		}
		mutex.requestCS();
		if (DEBUG_ON) {
			System.out.println("Proxy [" + myID + "] acquired Lamport Mutex");
		}
		//If have Mutex, Proceed
		ServerSocket mySocket = null;
		try {
			mySocket = new ServerSocket(myPort);
			this.listenerActive = true;
		}
		catch (IOException ie) {
			throw new IOException ("unable to bind to myPort [" + myPort + "]");
		}
		if (DEBUG_ON) {
			System.out.println("TCPProxy started on port  [" + myPort + "]");
			System.out.println("My Socket Address is  [" + InetAddress.getLocalHost().getHostAddress() + ":" + myPort + "]");
		}
		
		while (true) {
			try {
				Socket tcpClient = mySocket.accept();
				String clientAddress = tcpClient.getInetAddress().getHostAddress() + ":" + tcpClient.getPort();
				if (DEBUG_ON) {
					System.out.println("Accepted new Connection from  [" + clientAddress + "]");
				}
				TCPListenerThread listener = new TCPListenerThread(this, tcpClient, RCV_BUF);
				listener.start();
			}
			catch (Exception e) {
				//Bubble Up after closing mySocket
				mySocket.close();
				throw new Exception ("Some unexpected error occured.\n" + e.toString());
			}
		}
		
		
	}
	
	/*
	 * Wrapper method to start ServerPollerThread
	 */
	private void startServerPollerThread() {
		ServerPollerThread serverPoller = new ServerPollerThread(this);
		
		//http://www.linuxtopia.org/online_books/programming_books/thinking_in_java/TIJ315_005.htm
		//http://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
		//Mark Thread as Daemon
		serverPoller.setDaemon(true);
		serverPoller.start();
	}
	
	/*
	 * Wrapper method to start ProxyPollerThread
	 */
	private void startProxyPollerThread() {
		ProxyPollerThread proxyPoller = new ProxyPollerThread(this, mutex);
		
		//http://www.linuxtopia.org/online_books/programming_books/thinking_in_java/TIJ315_005.htm
		//http://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html
		//Mark Thread as Daemon
		proxyPoller.setDaemon(true);
		proxyPoller.start();
	}
	
	/*
	 * MAIN:
	 *  - ReadMyConfig
	 *  - startProxyPvtListenerThread
	 *  - startProxyPollerThread
	 *  - startServerPollerThread
	 *  - startTCPListenerThread
	 */
	
	public static void main(String[] args) {


	    if (args.length != 1) {
	        System.out.println("ERROR: Provide 1 argument");
	        System.out.println("\t(1) <id>: proxy's unique id");       
	        System.exit(-1);
	    }		
		
		TCPProxy proxy = new TCPProxy();
	    
		try {
			proxy.ReadMyConfig(args[0]);
			if (DEBUG_ON) {
				System.out.println("Get Target Poll Interval in Main: " + proxy.getPollIntervalMs());
			}
			proxy.startProxyPvtListenerThread();
			if (DEBUG_ON) {
				System.out.println("Started ProxyPvtListener thread");
			}
			proxy.startProxyPollerThread();
			if (DEBUG_ON) {
				System.out.println("Started ProxyPoller thread");
			}			
			proxy.startServerPollerThread();
			if (DEBUG_ON) {
				System.out.println("Started ServerPoller thread");
			}
			proxy.startTCPListenerThread();
			if (DEBUG_ON) {
				System.out.println("Started TCPListener thread");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
