

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SystemMaster {

	protected Shell shlSystemMaster;
	
	private static Color white, red, green; 
	private static Font font14, font12, font11;
	private static boolean server_0 = false;
	private static boolean server_1 = false;
	private static boolean proxy = false;
	private static boolean logger = false;
	static Server server0, server1;
	
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
		Display display = Display.getDefault();
		createContents();
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
	protected void createContents() {
		shlSystemMaster = new Shell();
		shlSystemMaster.setMinimumSize(new Point(450, 255));
		shlSystemMaster.setSize(450, 255);
		shlSystemMaster.setText("System Master");
		
		white = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		red = SWTResourceManager.getColor(204, 0, 0);
		green = SWTResourceManager.getColor(51,153,51);
		font14 = SWTResourceManager.getFont("Droid Sans", 14, SWT.BOLD);
		font12 = SWTResourceManager.getFont("Droid Sans", 12, SWT.BOLD);
		font11 = SWTResourceManager.getFont("Droid Sans", 11, SWT.BOLD);
		
		Label vSep = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.VERTICAL);
		vSep.setBounds(225, 10, 2, 211);
		
		Canvas bgLogger = new Canvas(shlSystemMaster, SWT.NONE);
		bgLogger.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		bgLogger.setBounds(235, 44, 205, 177);
		
		Label txtLogger = new Label(bgLogger, SWT.NONE);
		txtLogger.setFont(SWTResourceManager.getFont("Consolas", 12, SWT.NORMAL));
		txtLogger.setText("client:0,joke:0,part:0\nserver:0,joke:0,part:0\nclient:0,joke:0,part:1\nserver:0,joke:0,part:1");
		txtLogger.setBounds(10, 10, 185, 157);
		
		Label lblLogger = new Label(shlSystemMaster, SWT.NONE);
		lblLogger.setFont(font14);
		lblLogger.setBounds(233, 10, 73, 28);
		lblLogger.setText("Logger");
		
		Label lblProcesses = new Label(shlSystemMaster, SWT.NONE);
		lblProcesses.setText("Processes");
		lblProcesses.setFont(font14);
		lblProcesses.setBounds(10, 10, 73, 28);
		
		Label label = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 45, 205, 2);
		
		Label lblServer_1 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_1.setFont(font12);
		lblServer_1.setBounds(10, 98, 60, 23);
		lblServer_1.setText("Server 1");
		
		Label lblServer_0 = new Label(shlSystemMaster, SWT.NONE);
		lblServer_0.setText("Server 0");
		lblServer_0.setFont(font12);
		lblServer_0.setBounds(10, 62, 60, 28);
		
		Label lblProxy = new Label(shlSystemMaster, SWT.NONE);
		lblProxy.setText("Proxy");
		lblProxy.setFont(font12);
		lblProxy.setBounds(10, 155, 60, 28);
		
		Label lblLogger_1 = new Label(shlSystemMaster, SWT.NONE);
		lblLogger_1.setText("Logger");
		lblLogger_1.setFont(font12);
		lblLogger_1.setBounds(10, 193, 60, 28);
		
		Label label_1 = new Label(shlSystemMaster, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 138, 205, 2);
		
		Canvas semServer_0 = new Canvas(shlSystemMaster, SWT.NONE);
		semServer_0.setBackground(red);
		semServer_0.setBounds(78, 58, 24, 23);
		
		Canvas semServer_1 = new Canvas(shlSystemMaster, SWT.NONE);
		semServer_1.setBackground(red);
		semServer_1.setBounds(78, 96, 24, 23);
		
		Canvas semProxy = new Canvas(shlSystemMaster, SWT.NONE);
		semProxy.setBackground(red);
		semProxy.setBounds(78, 155, 24, 23);
		
		Canvas semLogger = new Canvas(shlSystemMaster, SWT.NONE);
		semLogger.setBackground(red);
		semLogger.setBounds(78, 193, 24, 23);
		
		Button btnServer_0 = new Button(shlSystemMaster, SWT.NONE);
		btnServer_0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(server_0){
					server_0 = false;
					semServer_0.setBackground(red);
					btnServer_0.setText("Start Server");
				}else{
					server_0 = true;
					semServer_0.setBackground(green);
					btnServer_0.setText("Kill Server");
				}
				shlSystemMaster.pack();
			}
		});
		btnServer_0.setFont(font11);
		btnServer_0.setBounds(118, 57, 94, 28);
		btnServer_0.setText("Start Server");
		
		Button btnServer_1 = new Button(shlSystemMaster, SWT.NONE);
		btnServer_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(server_1){
					server_1 = false;
					semServer_1.setBackground(red);
					btnServer_1.setText("Start Server");
				}else{
					server_1 = true;
					semServer_1.setBackground(green);
					btnServer_1.setText("Kill Server");
				}
				shlSystemMaster.pack();
			}
		});
		btnServer_1.setText("Start Server");
		btnServer_1.setFont(font11);
		btnServer_1.setBounds(118, 94, 94, 28);
		
		Button btnProxy = new Button(shlSystemMaster, SWT.NONE);
		btnProxy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(proxy){
					proxy = false;
					semProxy.setBackground(red);
					btnProxy.setText("Start Proxy");
				}else{
					proxy = true;
					semProxy.setBackground(green);
					btnProxy.setText("Kill Proxy");
				}
				shlSystemMaster.pack();
			}
		});
		btnProxy.setText("Start Proxy");
		btnProxy.setFont(font11);
		btnProxy.setBounds(118, 153, 94, 28);
		
		Button btnLogger = new Button(shlSystemMaster, SWT.NONE);
		btnLogger.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(logger){
					logger = false;
					semLogger.setBackground(red);
					btnLogger.setText("Start Logger");
				}else{
					logger = true;
					semLogger.setBackground(green);
					btnLogger.setText("Kill Logger");
				}
				shlSystemMaster.pack();
			}
		});
		btnLogger.setText("Start Logger");
		btnLogger.setFont(font11);
		btnLogger.setBounds(118, 192, 94, 28);

	}
}
