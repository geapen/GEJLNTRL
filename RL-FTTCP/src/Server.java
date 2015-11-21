
public class Server {
	
	static JokeRepo jokes = new JokeRepo();
	static int tcpPort = 8026;
	
	public static void main (String[] args) {

	    // TODO: handle request from proxy
	    TCPListener tcpListener = new TCPListener(jokes, tcpPort);
	    tcpListener.start();
	    try {
	    	tcpListener.join();
	    } catch (Exception e) { 
	        System.out.println("There was an exception in join: ");
	        e.printStackTrace();
	    }
	     
	}

}
