import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class ProxyPvtListenerThread extends Thread{
	
	private static boolean DEBUG_ON = false;
	
	private Socket proxyClient = null;
	private TCPProxy myProxy = null;
	private int myID;
	private Boolean joinAckRcvd;
	private LamportMutex mutex;
	
	public ProxyPvtListenerThread(int id, TCPProxy p, Socket s, Boolean joined, LamportMutex mutex) {


		this.proxyClient = s;
		this.myID = id;
		this.myProxy = p;
		this.joinAckRcvd = joined;
		this.mutex = mutex;
		this.DEBUG_ON = p.getDebugON();
	}
	
	public void run() {
		try {             
			BufferedReader in = new BufferedReader(new InputStreamReader(proxyClient.getInputStream()));
			
            String inputCmd = in.readLine();
			mutex.handleMessage(inputCmd);
		} 
		catch (IOException e) {
			System.err.println(e);
		}
	}
}