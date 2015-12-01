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
	private static boolean server_0 = false;
	private static boolean server_1 = false;
	private static boolean server_2 = false;
	private static boolean proxy_0 = false;
	static Process server0, server1, server2, proxy0;
	private Text txtLogger;
	private static ProcessLogger logger;
	
	private static boolean myFlag = false;
	
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
		shlSystemMaster.setMinimumSize(new Point(450, 500));
		shlSystemMaster.setSize(450, 500);
		shlSystemMaster.setText("System Master");
		
		white = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		red = SWTResourceManager.getColor(204, 0, 0);
		green = SWTResourceManager.getColor(51,153,51);
		font14 = SWTResourceManager.getFont("Droid Sans", 14, SWT.BOLD);
		font12 = SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD);
		font11 = SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD);
		
		Label vSep = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.VERTICAL);
		vSep.setBounds(134, 10, 2, 458);
		
		Canvas bgLogger = new Canvas(shlSystemMaster, SWT.NONE);
		bgLogger.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bgLogger.setBounds(142, 44, 298, 424);
		
		txtLogger = new Text(bgLogger, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtLogger.setFont(SWTResourceManager.getFont("Consolas", 10, SWT.NORMAL));
		txtLogger.setBounds(0, 10, 288, 404);
		
		Label lblLogger = new Label(shlSystemMaster, SWT.NONE);
		lblLogger.setFont(font14);
		lblLogger.setBounds(142, 7, 73, 28);
		lblLogger.setText("Log");
		
		Label lblProcesses = new Label(shlSystemMaster, SWT.NONE);
		lblProcesses.setText("Processes");
		lblProcesses.setFont(font14);
		lblProcesses.setBounds(10, 10, 73, 28);
		
		Label label = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 44, 118, 3);
		
		Label lblServer_1 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_1.setFont(font12);
		lblServer_1.setBounds(10, 186, 60, 23);
		lblServer_1.setText("Server 1");
		
		Label lblServer_0 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_0.setText("Server 0");
		lblServer_0.setFont(font12);
		lblServer_0.setBounds(10, 137, 60, 28);
		
		Label lblProxy = new Label(shlSystemMaster, SWT.NONE);
		lblProxy.setText("Proxy");
		lblProxy.setFont(font12);
		lblProxy.setBounds(10, 64, 60, 28);
		
		Label label_1 = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 111, 118, 2);
		
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
		btnServer_0.setBounds(72, 122, 49, 44);
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
		btnServer_1.setBounds(72, 174, 49, 44);
		
		Label lblServer_2 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_2.setText("Server 2");
		lblServer_2.setFont(SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD));
		lblServer_2.setBounds(10, 236, 60, 28);
		
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
		btnServer_2.setBounds(72, 226, 49, 44);
		
		Button btnProxy = new Button(shlSystemMaster, SWT.NONE);
		btnProxy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proxy_0){
					proxy_0 = false;
					btnProxy.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
					if(proxy0 != null)
						proxy0.destroy();
					
				}else{
					proxy_0 = true;
					btnProxy.setImage(SWTResourceManager.getImage(SystemMaster.class, "up.png"));
					proxy0 = startProxy();

//					Display.getDefault().asyncExec(new Runnable() {
//					    public void run() {
//					    	try{
//								InputStream in;
//								long millis = System.currentTimeMillis();
//								while( true){
//									if(millis % 3000 == 0){
//										in = proxy0.getInputStream();
//										byte b[]=new byte[in.available()];
//								        in.read(b,0,b.length);
//								        String text =  new String(b);
//								        String log = txtLogger.getText();
//										log = log + text + "";
//										txtLogger.setText(log);
//										System.out.println(log);
//										//Thread.sleep(3000 - millis % 3000);
//										myFlag = true;
//									}
//									millis = System.currentTimeMillis();
//								}
//							}catch(Exception error){
//								System.out.println("There was an exception reading the stream from thread");
//								error.printStackTrace();
//							}
//					    }
//					});
				}
				shlSystemMaster.pack();
			}
		});
		btnProxy.setImage(SWTResourceManager.getImage(SystemMaster.class, "down.png"));
		btnProxy.setBounds(72, 55, 49, 44);
		
		Button btnRefresh = new Button(shlSystemMaster, SWT.NONE);
		//btnRefresh.setImage(SWTResourceManager.getImage(SystemMaster.class, "/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png"));
		btnRefresh.setImage(SWTResourceManager.getImage(SystemMaster.class, "refresh.png"));
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String log = txtLogger.getText();
				if(proxy_0)
					log = log + getProcStream(proxy0) + "";
				else
					log = log + "Proxy is down." + "\n";
				txtLogger.setText(log);
			}
		});
		btnRefresh.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnRefresh.setBounds(353, 5, 41, 38);
		
		Button btnClear = new Button(shlSystemMaster, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtLogger.setText("");
			}
		});
		btnClear.setImage(SWTResourceManager.getImage(SystemMaster.class, "/com/sun/javafx/scene/web/skin/FontBackgroundColor_16x16_JFX.png"));
		btnClear.setFont(SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD));
		btnClear.setBounds(400, 5, 41, 38);
		
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
	public Process startProxy(){
		System.out.println("I'm starting Proxy");
		try{
			Process proc = Runtime.getRuntime().exec("java -jar src/TCPProxy.jar");
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
