import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class Client {

	protected Shell shell;
	static Label lblKnockKnock;
	static Button btnWhoIsThere;
	static Label lblPerson;
	static Button btnPersonWho;
	static Label lblJoke;
	static Button btnNextJoke;
	static Integer count = 0;
	//private Server server = new Server();
	static String hostAddress = "localhost";
	static Integer tcpPort = 8026;
	static BufferedReader in;
	static PrintWriter out;
	static Socket server;
	private Button btnBtnnextjoke;
	private Label lblCanoeHelpMe;
	private Label lblCanoe;
	private Button btnCanoeWho;
	private Label lblCanoeHelpMe_1;
	private Button btnNextJoke_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client window = new Client();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			Scanner sc = new Scanner(System.in);
			
		}catch (Exception e){
			
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.update();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(200, 160));
		shell.setSize(200, 160);
		shell.setText("Client GUI");
		generateFirstRound();

	}
	
	protected void generateFirstRound(){
		
		lblKnockKnock = new Label(shell, SWT.NONE);
		lblKnockKnock.setBounds(56, 10, 81, 17);
		lblKnockKnock.setAlignment(SWT.CENTER);
		lblKnockKnock.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		lblKnockKnock.setText("Knock, Knock.");
		
		btnWhoIsThere = new Button(shell, SWT.NONE);
		btnWhoIsThere.setBounds(43, 40, 104, 28);
		btnWhoIsThere.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generateSecondRound(callTCPServer(count.toString()+" 0"));
				shell.pack();
			}
		});
		btnWhoIsThere.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnWhoIsThere.setText("Who is there?");
	}
	
	protected void generateSecondRound(String person){
		lblPerson = new Label(shell, SWT.NONE);
		lblPerson.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		lblPerson.setAlignment(SWT.CENTER);
		lblPerson.setText(person+".");
		lblPerson.setBounds(56, 84, 81, 17);
		
		btnPersonWho = new Button(shell, SWT.NONE);
		btnPersonWho.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnPersonWho.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generateThirdRound(callTCPServer(count.toString()+" 1"));
				shell.pack();
			}
		});
		btnPersonWho.setText(person+", who?");
		btnPersonWho.setBounds(43, 122, 104, 28);
		
	}
	
	protected void generateThirdRound(String joke){
		lblJoke = new Label(shell, SWT.WRAP);
		lblJoke.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		lblJoke.setAlignment(SWT.CENTER);
		lblJoke.setText(joke);
		lblJoke.setBounds(20, 171, 159, 50);
		
		
		btnNextJoke = new Button(shell, SWT.NONE);
		btnNextJoke.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnNextJoke.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				count ++;
				clearDisplay();
				generateFirstRound();
				shell.pack();
			}
		});
		btnNextJoke.setText("Next Joke");
		btnNextJoke.setBounds(43, 222, 104, 28);
	}
	
	protected void clearDisplay(){
		lblKnockKnock.dispose();
		btnWhoIsThere.dispose();
		lblPerson.dispose();
		btnPersonWho.dispose();
		lblJoke.dispose();
		btnNextJoke.dispose();
	}
	
	static public String callTCPServer (String cmd) {
		//Set debug return message by default
		String retValue = "**Error**";
		String host = hostAddress;
		Integer port = tcpPort;
		try {
			server = new Socket(host, port);
			out = new PrintWriter(server.getOutputStream(), true);
			in =  new BufferedReader(new InputStreamReader(server.getInputStream()));
			out.println(cmd);
			retValue = in.readLine();
			server.close();
		} 
		catch (UnknownHostException e) {
		    System.err.println("Unknown host " + host + e);
		}
		catch (IOException e) {
			System.err.println("Server aborted:" + e);
			}
		return retValue;
	 }
}
