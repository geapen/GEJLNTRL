import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Properties;

public class TCPProxy {
	private static final boolean DEBUG_ON = true;
	public static final String PROPERTIES_FILE = "config.properties";
	private int myPort;
	//http://javahungry.blogspot.com/2015/03/difference-between-array-and-arraylist-in-java-example.html
	//private ServerTarget[] arServerTargets = null;
	private ArrayList<ServerTarget> serverTargetList;
	private long pollIntervalMs;

	public long getPollIntervalMs() {
		return this.pollIntervalMs;
	}
	
	public ArrayList<ServerTarget> getServerTargetList() {
		return this.serverTargetList;
	}
	
	//http://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
	public void ReadMyConfig() throws IOException{
		InputStream inputStream = null;
		try {
			Properties props = new Properties();
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 

			// get the property value and print it out
			String serverTargets = props.getProperty("serverTargets");
			this.myPort = Integer.parseInt(props.getProperty("myPort"));
			if (DEBUG_ON) {
				System.out.println(this.myPort); 
			}
			this.pollIntervalMs = Long.parseLong(props.getProperty("pollIntervalMs"));
			if (DEBUG_ON) {
				System.out.println(this.pollIntervalMs); 
			}			
			serverTargetList = new ArrayList<ServerTarget>();
			StringTokenizer stServers = new StringTokenizer(serverTargets,",");
			while (stServers.hasMoreTokens()) {
				String serverPort = stServers.nextToken();
				if (DEBUG_ON) {
					System.out.println(serverPort);
				}
				ServerTarget target = new ServerTarget();
				target.setAddress(serverPort);
				serverTargetList.add(target);
				
			}
			
			

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
	}
	
	/*
	 * Starts ListenerThread after binding to listening Port designated for TCPProxy
	 */
	public void startTCPListenerThread() throws Exception {
		//bind to myPort
		ServerSocket mySocket = null;
		try {
			mySocket = new ServerSocket(myPort);
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
				TCPListenerThread listener = new TCPListenerThread(this, tcpClient);
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
	 * Wrapper method to start IsOnlineThread
	 */
	private void startIsTargetOnlineThread() {
		IsOnlineThread isOnlineThread = new IsOnlineThread(this);
		isOnlineThread.setDaemon(true);
		isOnlineThread.start();
	}
	
	/*
	 * MAIN:
	 *  - ReadMyConfig
	 *  - startIsTargetOnlineThread
	 *  - startTCPListenerThread
	 */
	
	public static void main(String[] args) {
		TCPProxy proxy = new TCPProxy();
		try {
			proxy.ReadMyConfig();
			if (DEBUG_ON) {
				System.out.println(proxy.getPollIntervalMs());
			}
			proxy.startIsTargetOnlineThread();
			if (DEBUG_ON) {
				System.out.println("Started isOnline polling thread");
			}
			proxy.startTCPListenerThread();
			if (DEBUG_ON) {
				System.out.println("Started Listener thread");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
