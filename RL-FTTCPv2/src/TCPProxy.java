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
	private long proxyPollIntervalMs;
	private LamportMutex mutex;
	private DirectClock clock;
	Long[] lastPingRcvd;

	public long getPollIntervalMs() {
		return this.pollIntervalMs;
	}
	
	public long getProxyPollIntervalMs() {
		return this.proxyPollIntervalMs;
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
	
	public long getlastPingRcvd(int k) {
		return lastPingRcvd[k];
	}
	
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
		int numProxy = 0;
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
			this.proxyPollIntervalMs = Long.parseLong(props.getProperty("proxyPollIntervalMs"));
			if (DEBUG_ON) {
				System.out.println("Proxy Poll Interval: " + this.proxyPollIntervalMs); 
			}
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
				//Initially set online and let Poller set Offline so Lamport works
				target.setOnline();
				proxyTargetList.add(target);
			}
			//since can extract from size, no need to store explicitly
			numProxy = proxyTargetList.size();
			this.myPvtPort = proxyTargetList.get(myID).getPort();
			this.clock = new DirectClock(numProxy, myID);
			//Initialize lastPingRcvd to 0
			this.lastPingRcvd = new Long[numProxy];
			for (int k = 0; k < numProxy; k++) {
				this.lastPingRcvd[k] = 0L;
			}
			this.mutex = new LamportMutex(myID, clock, proxyTargetList, lastPingRcvd, joinAckReceived);

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
	}
	
	/*
	 * Starts ProxyPvtListenerThread after binding to private listening Port designated for TCPProxy
	 */
	public void startPrivateListener() throws Exception {
		
		//PrivateListener(int id, TCPProxy p,  Boolean joined, DirectClock clock, LamportMutex mutex)
		PrivateListener pvtListener = new PrivateListener(myID,this,joinAckReceived,clock,mutex);
		pvtListener.setDaemon(true);
		pvtListener.start();
		
	}
	
	/*
	 * Starts ListenerThread after binding to listening Port designated for TCPProxy
	 */
	public void startTCPListenerThread() throws Exception {
		//bind to myPort
		//block here with Lamport?
		//Wait for warmup period (PollInterval)
		try {
			Thread.sleep(pollIntervalMs);
		} catch (InterruptedException ie) 	{
			ie.printStackTrace();
		}
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
		ProxyPollerThread proxyPoller = new ProxyPollerThread(this, clock, mutex);
		
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
				System.out.println("Get Proxy Internal ID: " + proxy.getMyID());
			}
			proxy.startPrivateListener();
			if (DEBUG_ON) {
				System.out.println("Started PrivateListener");
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