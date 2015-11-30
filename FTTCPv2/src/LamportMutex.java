import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class LamportMutex {
	int[] myQ; // request queue
	int myId;
	private DirectClock clock;
	private ArrayList<ServerTarget> proxiesList;
	Boolean joinAckRcvd;
	Long[] lastPingRcvd;
	
	public LamportMutex(int id, DirectClock clock, ArrayList<ServerTarget> list, Long[] lpr, Boolean joined ) {
		this.clock = clock;
		this.myQ = new int[list.size()];
		this.proxiesList = list;
		this.myId = id;
		this.lastPingRcvd = lpr;
		joinAckRcvd = joined;
		
		//each slot in queue array represents process id at that index.
		//the integer value in each slot determines priority
		//initialize every process to lowest possible priority
		
		for (int j = 0; j < proxiesList.size(); j++){
			//Instead of Symbols.Infinity
			myQ[j] = Integer.MAX_VALUE;
		}
		
	}
	
	public synchronized void requestCS() {
		try {
			//ticks the clock
			clock.sendAction();
			
			myQ[myId] = clock.getValue(myId);
			
			//syntax: mutex {request | release | ok} <my id> <my clock value> <my timestamp>
			broadcastMessage("request_cs " + myId + " " + clock.getValue(myId) + " " + myQ[myId]);
			while (!okayCS()){
				//Remove Debug Stmt
				//printQueue();
				//printClock();
				//System.out.println("TCPProxy [" + this.myId + "] okayCS was false. Waiting...");
				wait();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void releaseCS() {
		myQ[myId] = Integer.MAX_VALUE;
		//syntax: mutex {request_cs | release_cs | ack_req | join | join_ack} <my id> <my clock value> <my timestamp>
		broadcastMessage("release_cs " + myId + " " + clock.getValue(myId) + " " + myQ[myId]);
	}
	
	boolean okayCS() {
		for (int j = 0; j < proxiesList.size(); j++){
			//if other proxies are offline, skip
			if (proxiesList.get(j).isOnline() ) {
				if (isGreater(myQ[myId], myId, myQ[j], j)){
					return false;
				}
					
				if (isGreater(myQ[myId], myId, clock.getValue(j), j)){
					return false;
				}
			}		
		}
		return true;
	}
	
	//returns true if first entry1 is greater
	boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
		if (entry2 == Integer.MAX_VALUE){
			return false;
		}
		
		return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
	}
	
	public void broadcastMessage(String message){
		//send message to everyone but me

		int proxy_index = 0;
		for(ServerTarget server : proxiesList){
			//only send messages to servers that aren't me.
			if(proxy_index != myId){
				sendMessage(message, server);
			}
			
			proxy_index++;
		}
	}
	
	public void sendMessage(String message, ServerTarget dest){
		try {
			clock.sendAction();
			//open a TCP socket to dest, send the message, and close the socket.
			InetAddress ia = InetAddress.getByName(dest.getHostName());
			Socket socket;
			socket = new Socket(ia, dest.getPort());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			//Remove Debug Stmt
			//System.out.println("to port: "+ dest.getPort() + ":" + message); 
			out.write(message);
			out.flush();
			socket.close();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//Remove Debug Stmt
			//System.out.println("Could not send server message to " + dest.hostname + ":" + dest.port);
		}
	}
	
	//syntax: mutex {request_cs | release_cs | ack_req | join | join_ack} <my id> <my clock value> <my timestamp>	//examples: mutex request 0  3
	public synchronized void handleMessage(String message) {
		//System.out.println("in handleMessage: " + message);
		String[] messageArray = message.split(" ");

		String command = messageArray[0];
		
		//System.out.println("got message from server elements:"+messageArray.length);
		//id of source server
		int msg_id = Integer.parseInt(messageArray[1]);
	
		//clock timestamp
		int msg_clock = Integer.parseInt(messageArray[2]);
	
		//queue timestamp
		int msg_timestamp = Integer.parseInt(messageArray[3]);
			
		clock.receiveAction(msg_id, msg_clock);
	
	/*
		 * 	
	REQUEST_CS, //Request CS
	ACK_REQ,    //Acknowledge receipt of request of rCS
	RELEASE_CS, //Release CS
	JOIN,       //Server instance join
	JOIN_ACK,   //Acknowledgement for Server instance join
	PING        //Echo message for fault tolerance 
		 */
	
		switch (command) {
			case "request_cs":
				myQ[msg_id] = msg_clock;
				sendMessage("ack_req " + myId + " " + clock.getValue(myId) + " " + myQ[myId], proxiesList.get(msg_id));			
			break;
			case "ack_req":
				// ack <my id> <my clock value>
				int ack_id = Integer.parseInt(messageArray[1]);
				int ack_clock = Integer.parseInt(messageArray[2]);
				
				myQ[msg_id] = msg_timestamp;
				
			break;
			case "release_cs":
				myQ[msg_id] = Integer.MAX_VALUE;
			break;
			case "join":
				//System.out.println("someone want to join");
				if (!joinAckRcvd) {				
					joinAckRcvd = Boolean.TRUE;
				}
				System.out.println("ack_join " + myId + " " + clock.getValue(myId) + " " + myQ[myId] + " -> " + msg_id);
				sendMessage("ack_join " + myId + " " + clock.getValue(myId) + " " + myQ[myId] , proxiesList.get(msg_id));
			break;				
			case "ack_join":
				//System.out.println("msglength"+messageArray.length);
				if (!joinAckRcvd) {
					//System.out.println("notjoinedAck");
					joinAckRcvd = Boolean.TRUE;
				}
			break;
			case "ping":
				//Remove Debug Stmt
				//System.out.println("Current Ping Value:[" + msg_id + "]: " + lastPingRcvd[msg_id]); 
				lastPingRcvd[msg_id] = System.currentTimeMillis();
				//Remove Debug Stmt
				//System.out.println("After Receive Ping Value:[" + msg_id + "]: " + lastPingRcvd[msg_id]); 
			break;	
			default:
				throw new IllegalArgumentException("invalid message command prefix: " + message);
		}
		

		notify(); // okayCS() may be true now
	}
	
	public synchronized boolean startClientListenerOK () {
		requestCS();
		//Return True if have CS
		return true;
	}
	
	/**
	 * debug function to see state of queue
	 */
	private void printQueue(){
		System.out.print("TCPProxy [" + this.myId + "] queue timestamps: ");
		for(int i = 0; i < myQ.length; i++){
			System.out.print(i + "["+ myQ[i] +"] ");
		}
	}
	
	private void printClock(){
		System.out.print("server [" + this.myId + "] clock timestamps: ");
		for(int i = 0; i < clock.clock.length; i++){
			System.out.print(i + "["+ clock.getValue(i) +"] ");
		}
		System.out.println("");
	}
}
