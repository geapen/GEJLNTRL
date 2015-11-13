
public class ServerTarget {
	String hostname;
	int port;
	int numClients = 0;
	boolean online = true;
	public ServerTarget(String s, int i){
		this.hostname = new String (s);
		this.port = i;
	}

	public ServerTarget() {
		// TODO Auto-generated constructor stub
		
	}
	
	//parses string like "192.168.1.101:3033" to setAddress
	public void setAddress(String address){
		String[] addrSplit = address.split(":");
		if(addrSplit.length != 2){
			throw new IllegalArgumentException("Bad address format.");
		} else {
			this.hostname = addrSplit[0];
			this.port = Integer.parseInt(addrSplit[1]);
		}
	}
	
	public String getHostName () {
		return hostname;
	}
	
	public int getPort() {
		return port;
	}
	
	public boolean isAlive() {
		return online;
	}
	
	public int clientCount() {
		return numClients;
	}
	
	public void incrementClientCount() {
		numClients++;
	}
	
	public void decrementClientCount() {
		if (numClients > 0) numClients--;
	}
	
	public void setOffline() {
		online = false;
	}
	
	public void setOnline() {
		online = true;
	}
}
