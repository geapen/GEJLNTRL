import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ProxyPollerThread  extends Thread {
	private TCPProxy myProxy = null;
	private LamportMutex mutex;
	
	public ProxyPollerThread(TCPProxy p, LamportMutex mutex) {
		this.myProxy = p;
		this.mutex = mutex;
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(myProxy.getPollIntervalMs());
			} catch (InterruptedException ie) 	{
				ie.printStackTrace();
			}
			//if currently active instance, do nothing
			if (!myProxy.isListenerActive()) {
				PollProxies();
			}
		}
	}
	
	private void PollProxies() {
		ArrayList<ServerTarget> proxiesList = myProxy.getProxyTargetList();
		
		int proxy_index = 0;
		for(ServerTarget proxy : proxiesList){
			//only send messages to servers that aren't me.
			if(proxy_index != myProxy.getMyID()){
				if (!IsOnline(proxy)) {
					 proxy.setOffline();
					 //Call Release_CS on behalf of offline proxy in case it is holding a lock.  This will update my local queue
					 //syntax: mutex {request_cs | release_cs | ack_req | join | join_ack} <my id> <my clock value> <my timestamp>
					 String specialMessage = "release_cs " + proxy_index + " " + 0 + " " + 0;
					 mutex.handleMessage(specialMessage);
				}
				else {
					if (!proxy.isOnline()) {
						proxy.setOnline();
					}
				}
			}
			
			proxy_index++;
		}
		
	}
	
	private boolean IsOnline(ServerTarget t) {
		boolean result = false;
		Socket s;
		try {
			s = new Socket (t.getHostName(), t.getPort());
			result = true;
			s.close();
		}
		catch (IOException ie){
			//ie.printStackTrace();
			if (myProxy.getDebugON()) {
				System.out.println("A TCPProxy Instance Offline ["+t.getHostName()+":"+t.getPort()+"]");
			}
			result = false;
		}

		return result;
	}
}
