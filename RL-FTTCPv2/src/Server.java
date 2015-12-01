
public class Server {
	
	static JokeRepo jokes = new JokeRepo();
	static int tcpPort;
	
	public static void main (String[] args) {
		if (args.length != 1) {
	      System.out.println("ERROR: Provide 1 arguments");
	      System.out.println("\t(1) <port>: the port number for TCP connection");
	      System.exit(-1);
	    }

	    tcpPort = Integer.parseInt(args[0]);
		    
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
