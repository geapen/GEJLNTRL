import java.net.*; 
import java.io.*; 
import java.util.*;

public class TCPServerThread extends Thread {

	Socket theClient;
	JokeRepo repo;
	
	public TCPServerThread(JokeRepo repo, Socket s) {
		// TODO Auto-generated constructor stub
		this.repo = repo;
		this.theClient = s;
	}
	
	public void run() {
		try {
			String returnMsg = "Thread Error";
			PrintWriter out = new PrintWriter(theClient.getOutputStream(), true);                   
			BufferedReader in = new BufferedReader(new InputStreamReader(theClient.getInputStream()));
			
            String inputCmd = in.readLine();
			
			System.out.println("received:" + inputCmd);
			Scanner st = new Scanner(inputCmd);          
			String joke = st.next();
			String part = st.next();
			
			if(part.equals("0"))
				returnMsg = whoIsThere(Integer.parseInt(joke));
			else if (part.equals("1"))
				returnMsg = personWho(Integer.parseInt(joke));
			else
				returnMsg = "Wrong command.";
			out.println(returnMsg);
			st.close();
			theClient.close();
		} catch (IOException e) {
			System.err.println(e);
		}

	}
	
	public String whoIsThere(int cmd){
		String joke = repo.getWhoIsThere(cmd);
		return joke;
	}
	
	public String personWho(int cmd){
		String joke = repo.getPersonWho(cmd);
		return joke;
	}

}