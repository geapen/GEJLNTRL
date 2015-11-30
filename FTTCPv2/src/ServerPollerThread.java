
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerPollerThread extends Thread {
	private TCPProxy myProxy = null;
	
	public ServerPollerThread(TCPProxy p) {
		this.myProxy = p;
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(myProxy.getPollIntervalMs());
			} catch (InterruptedException ie) 	{
				ie.printStackTrace();
			}
			PollOfflineServers();
		}
	}
	
	private void PollOfflineServers() {
		ArrayList<ServerTarget> TargetList = myProxy.getServerTargetList();
		Iterator<ServerTarget> it = TargetList.iterator();
		while (it.hasNext()) {
			ServerTarget target = it.next();
			if (!target.isOnline()) {
				if (IsOnline(target)) {
					target.setOnline();
				}
			}
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
				System.out.println("Server Target Offline ["+t.getHostName()+":"+t.getPort()+"]");
			}
			result = false;
		}

		return result;
	}
	
}
