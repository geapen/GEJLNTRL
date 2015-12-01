import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.io.InputStream;
import java.util.logging.Handler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class SystemMaster {

	protected Shell shlSystemMaster;
	
	private static Color white, red, green; 
	private static Font font14, font12, font11;
	private static boolean server_0 = false, server_1 = false, server_2 = false, proxy_0 = false, proxy_1 = false, proxy_2 = false;
	static Process server0, server1, server2, proxy0, proxy1, proxy2;
	private Text txtLog_0;
	private static int[] proxyStar = new int[] {190, 403, 615}; //Proxy0 190 | Proxy1 403 | Proxy2 615
	
	
	private static boolean myFlag = false;
	private Text txtLog_1;
	private Text txtLog_2;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SystemMaster window = new SystemMaster();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display.setAppName("Fault Tolerant Proxy TCP");
		Display display = Display.getDefault();
		
		createContents(display);
		shlSystemMaster.open();
		shlSystemMaster.layout();
		while (!shlSystemMaster.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shlSystemMaster = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlSystemMaster.setMinimumSize(new Point(778, 500));
		shlSystemMaster.setSize(778, 500);
		shlSystemMaster.setText("System Master");
		
		white = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		red = SWTResourceManager.getColor(204, 0, 0);
		green = SWTResourceManager.getColor(51,153,51);
		font14 = SWTResourceManager.getFont("Droid Sans", 14, SWT.BOLD);
		font12 = SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD);
		font11 = SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD);
		
		Label vSep = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.VERTICAL);
		vSep.setBounds(134, 10, 2, 458);
		
		Canvas bgLog_0 = new Canvas(shlSystemMaster, SWT.NONE);
		bgLog_0.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bgLog_0.setBounds(142, 95, 200, 373);
		
		txtLog_0 = new Text(bgLog_0, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtLog_0.setFont(SWTResourceManager.getFont("Consolas", 10, SWT.NORMAL));
		txtLog_0.setBounds(0, 10, 190, 353);
		
		Label lblLogs = new Label(shlSystemMaster, SWT.NONE);
		lblLogs.setFont(font14);
		lblLogs.setBounds(142, 7, 118, 28);
		lblLogs.setText("Logs");
		
		Label lblProcesses = new Label(shlSystemMaster, SWT.NONE);
		lblProcesses.setText("Processes");
		lblProcesses.setFont(font14);
		lblProcesses.setBounds(10, 10, 73, 28);
		
		Label label = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 44, 118, 3);
		
		Label lblServer_1 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_1.setFont(font12);
		lblServer_1.setBounds(10, 347, 60, 23);
		lblServer_1.setText("Server 1");
		
		Label lblServer_0 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_0.setText("Server 0");
		lblServer_0.setFont(font12);
		lblServer_0.setBounds(10, 297, 60, 28);
		
		Label lblProxy_0 = new Label(shlSystemMaster, SWT.NONE);
		lblProxy_0.setText("Proxy 0");
		lblProxy_0.setFont(font12);
		lblProxy_0.setBounds(10, 81, 60, 28);
		
		Label lblProxy_1 = new Label(shlSystemMaster, SWT.NONE);
		lblProxy_1.setText("Proxy 1");
		lblProxy_1.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblProxy_1.setBounds(10, 133, 60, 28);
		
		Label lblProxy_2 = new Label(shlSystemMaster, SWT.NONE);
		lblProxy_2.setText("Proxy 2");
		lblProxy_2.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblProxy_2.setBounds(10, 186, 60, 28);
		
		Label label_1 = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 250, 118, 2);
		
		Button btnServer_0 = new Button(shlSystemMaster, SWT.NONE);
		btnServer_0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(server_0){
					server_0 = false;
					btnServer_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(server0 != null)
						server0.destroy();
				}else{
					server_0 = true;
					btnServer_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					server0 = startServer("8025");
				}
				shlSystemMaster.pack();
			}
		});
		btnServer_0.setBounds(72, 282, 49, 44);
		btnServer_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		
		Button btnServer_1 = new Button(shlSystemMaster, SWT.NONE);
		btnServer_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(server_1){
					server_1 = false;
					btnServer_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(server1 != null)
						server1.destroy();
				}else{
					server_1 = true;
					btnServer_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					server1 = startServer("8035");
				}
				shlSystemMaster.pack();
			}
		});
		btnServer_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnServer_1.setBounds(72, 334, 49, 44);
		
		Label lblServer_2 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_2.setText("Server 2");
		lblServer_2.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblServer_2.setBounds(10, 396, 60, 28);
		
		Button btnServer_2 = new Button(shlSystemMaster, SWT.NONE);
		btnServer_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(server_2){
					server_2 = false;
					btnServer_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(server2 != null)
						server2.destroy();
				}else{
					server_2 = true;
					btnServer_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					server2 = startServer("8045");
				}
				shlSystemMaster.pack();
			}
		});
		btnServer_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnServer_2.setBounds(72, 386, 49, 44);
		
		Button btnProxy_0 = new Button(shlSystemMaster, SWT.NONE);
		btnProxy_0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proxy_0){
					proxy_0 = false;
					btnProxy_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(proxy0 != null)
						proxy0.destroy();
					
				}else{
					proxy_0 = true;
					btnProxy_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					proxy0 = startProxy(1);
				}
				shlSystemMaster.pack();
			}
		});
		btnProxy_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnProxy_0.setBounds(72, 72, 49, 44);
		
		Button btnProxy_1 = new Button(shlSystemMaster, SWT.NONE);
		btnProxy_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proxy_1){
					proxy_1 = false;
					btnProxy_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(proxy1 != null)
						proxy1.destroy();
					
				}else{
					proxy_1 = true;
					btnProxy_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					proxy1 = startProxy(2);
				}
				shlSystemMaster.pack();
			}
		});
		btnProxy_1.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnProxy_1.setBounds(72, 122, 49, 44);
		
		Button btnProxy_2 = new Button(shlSystemMaster, SWT.NONE);
		btnProxy_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proxy_2){
					proxy_2 = false;
					btnProxy_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(proxy2 != null)
						proxy2.destroy();
					
				}else{
					proxy_2 = true;
					btnProxy_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					proxy2 = startProxy(3);
				}
				shlSystemMaster.pack();
			}
		});
		btnProxy_2.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnProxy_2.setBounds(72, 172, 49, 44);
		
		Label starredProxy = new Label(shlSystemMaster, SWT.NONE);
		starredProxy.setImage(SWTResourceManager.getImage(SystemMaster.class, "star.png"));
		//starredProxy.setBounds(proxyStar[0], 55, 25, 31); 
		
		Button btnRefresh_0 = new Button(shlSystemMaster, SWT.NONE);
		btnRefresh_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "refresh.png"));
		btnRefresh_0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String log = txtLog_0.getText();
				if(proxy_0){
					String stream = getProcStream(proxy0);
					if(stream.contains("acquired Lamport Mutex"))
						starredProxy.setBounds(proxyStar[0], 55, 25, 31); 
					log = log + stream + "";
				}
				else
					log = log + "Proxy is down." + "\n";
				txtLog_0.setText(log);
				log = txtLog_1.getText();
				if(proxy_1){
					String stream = getProcStream(proxy1);
					if(stream.contains("acquired Lamport Mutex"))
						starredProxy.setBounds(proxyStar[1], 55, 25, 31); 
					log = log + stream + "";
				}
				else
					log = log + "Proxy is down." + "\n";
				txtLog_1.setText(log);
				log = txtLog_2.getText();
				if(proxy_2){
					String stream = getProcStream(proxy2);
					if(stream.contains("acquired Lamport Mutex"))
						starredProxy.setBounds(proxyStar[2], 55, 25, 31); 
					log = log + stream + "";
				}
				else
					log = log + "Proxy is down." + "\n";
				txtLog_2.setText(log);
			}
		});
		btnRefresh_0.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnRefresh_0.setBounds(677, 4, 41, 38);
		
		Button btnClear_0 = new Button(shlSystemMaster, SWT.NONE);
		btnClear_0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtLog_0.setText("");
				txtLog_1.setText("");
				txtLog_2.setText("");
			}
		});
		btnClear_0.setImage(SWTResourceManager.getImage(SystemMaster.class, "/com/sun/javafx/scene/web/skin/FontBackgroundColor_16x16_JFX.png"));
		btnClear_0.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnClear_0.setBounds(724, 4, 41, 38);

		
		Label lblLogProxy_0 = new Label(shlSystemMaster, SWT.NONE);
		lblLogProxy_0.setText("Proxy 0");
		lblLogProxy_0.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblLogProxy_0.setBounds(142, 61, 60, 28);
		
		Label label_2 = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setBounds(142, 44, 631, 3);
		
		Canvas bgLog_1 = new Canvas(shlSystemMaster, SWT.NONE);
		bgLog_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bgLog_1.setBounds(355, 95, 200, 373);
		
		txtLog_1 = new Text(bgLog_1, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtLog_1.setFont(SWTResourceManager.getFont("Consolas", 10, SWT.NORMAL));
		txtLog_1.setBounds(0, 10, 190, 353);
		
		Canvas bgLog_2 = new Canvas(shlSystemMaster, SWT.NONE);
		bgLog_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bgLog_2.setBounds(568, 95, 200, 373);
		
		txtLog_2 = new Text(bgLog_2, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtLog_2.setFont(SWTResourceManager.getFont("Consolas", 10, SWT.NORMAL));
		txtLog_2.setBounds(0, 10, 190, 353);
		
		Label lblLogProxy_1 = new Label(shlSystemMaster, SWT.NONE);
		lblLogProxy_1.setText("Proxy 1");
		lblLogProxy_1.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblLogProxy_1.setBounds(355, 61, 60, 28);
		
		Label lblLogProxy_2 = new Label(shlSystemMaster, SWT.NONE);
		lblLogProxy_2.setText("Proxy 2");
		lblLogProxy_2.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblLogProxy_2.setBounds(568, 61, 60, 28);
		
	}
	
	public Process startServer(String port){
		System.out.println("I'm starting Server: "+port);
		try{
			Process proc = Runtime.getRuntime().exec("java -jar src/Server.jar "+port);
			InputStream in = proc.getInputStream();
			byte b[]=new byte[in.available()];
	        in.read(b,0,b.length);
	        System.out.println(new String(b));
			InputStream err = proc.getErrorStream();
			byte e[]=new byte[err.available()];
	        err.read(e,0,e.length);
	        System.out.println(new String(e));
	        return proc;
	        
		}catch(Exception error){
			System.out.println("There was an exception running the JAR: "+error);
		}
		return null;
	}
	public Process startProxy(int id){
		System.out.println("I'm starting Proxy");
		try{
			Process proc = Runtime.getRuntime().exec("java -jar src/TCPProxy.jar " +id);
			InputStream in = proc.getInputStream();
			byte b[]=new byte[in.available()];
	        in.read(b,0,b.length);
	        String sb = new String(b);
	        System.out.println(sb);
			InputStream err = proc.getErrorStream();
			byte e[]=new byte[err.available()];
	        err.read(e,0,e.length);
	        System.out.println(new String(e));
	        return proc;
	        
		}catch(Exception error){
			System.out.println("There was an exception running the JAR: "+error);
		}
		return null;
	}
	
	public String getProcStream(Process proc){
		try{
			InputStream in = proc.getInputStream();
			byte b[]=new byte[in.available()];
	        in.read(b,0,b.length);
	        return new String(b);
		}catch(Exception error){
			System.out.println("There was an exception reading the stream");
		}
		return null;
	}
}
