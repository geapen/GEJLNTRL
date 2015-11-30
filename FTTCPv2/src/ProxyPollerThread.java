import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ProxyPollerThread  extends Thread {
	private TCPProxy myProxy = null;
	private LamportMutex mutex;
	private DirectClock clock;
	
	public ProxyPollerThread(TCPProxy p, DirectClock c, LamportMutex mutex) {
		this.myProxy = p;
		this.mutex = mutex;
		this.clock = c;
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			PollProxies();
			try {
				Thread.sleep(myProxy.getProxyPollIntervalMs());
			} catch (InterruptedException ie) 	{
				ie.printStackTrace();
			}
			//if currently active instance, do nothing
			//if (!myProxy.isListenerActive()) {
				
			//}
		}
	}
	
	private void PollProxies() {
		ArrayList<ServerTarget> proxiesList = myProxy.getProxyTargetList();
		int myID = myProxy.getMyID();
		int proxy_index = 0;
		for(ServerTarget proxy : proxiesList){
			//only send messages to servers that aren't me.
			if(proxy_index != myID){
				mutex.broadcastMessage("ping " + myID + " " + clock.getValue(myID) + " " + Integer.MAX_VALUE );
				if (!IsOnline(proxy_index)) {
					//Mark Proxy as Offline after 2 failed heartbeats.  This is used by Lamport in OkayCS
					proxy.setOffline();
					//Call Release_CS on behalf of dead proxy in case it is holding a lock.  This will update my local queue
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
	
	private boolean IsOnline(int index) {
		boolean result = true;
		//3 x proxyPollIntervalMs
		long isAlivePollInterval = 3 * myProxy.getProxyPollIntervalMs();
		Long myCurrentTime = System.currentTimeMillis();
		
		if (myCurrentTime - myProxy.getlastPingRcvd(index) > isAlivePollInterval) {
			result = false;
		}
	
		return result;
	}
}
