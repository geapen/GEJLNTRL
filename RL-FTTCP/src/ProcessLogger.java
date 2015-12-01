import java.net.*; 
import java.io.*; 
import java.util.*;
import org.eclipse.swt.widgets.Text;

public class ProcessLogger extends Thread{

	Process proc;
	Text textArea;
	
	public ProcessLogger(Process proc, Text textArea) {
		// TODO Auto-generated constructor stub
		
		this.proc = proc;
		this.textArea = textArea;
	}
	
	public void run() {
		try{
			InputStream in;
			while( ( in = proc.getInputStream() ) != null){
				byte b[]=new byte[in.available()];
		        in.read(b,0,b.length);
		        String text =  new String(b);
		        String log = textArea.getText();
				log = log + text + "";
				textArea.setText(log);
			}
		}catch(Exception error){
			System.out.println("There was an exception reading the stream from thread");
			error.printStackTrace();
		}
		
	}

}