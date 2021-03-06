/*
 * TCPStreamFwdThread handles the TCP stream forwarding between client sockets and server
 * sockets on TCPProxy 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TCPStreamFwdThread extends Thread {
	//Start 8K buffer but play increasing to 64K later
	//https://en.wikipedia.org/wiki/TCP_tuning
	//http://stackoverflow.com/questions/2811006/what-is-a-good-buffer-size-for-socket-programming
	private static int RCV_BUF = 8192;
	
	private InputStream in = null;
	private OutputStream out = null;
	TCPListenerThread listener = null;
	// 1 = ClientStreamProxy, 2 = ServerStreamProxy
	private StreamSource  src;
	
	public void setInputStream (InputStream i) {
		this.in = i;
	}
	
	public void setOutputStream (OutputStream o) {
		this.out = o;
	}
	
	/*
	 * Create a TCPStream forwarding thread mapping listener's input to output (client/server)
	 */
	public TCPStreamFwdThread (TCPListenerThread l, InputStream i, OutputStream o, StreamSource s, int receiveBufferSize) {
		this.in = i;
		this.out = o;
		this.listener = l;
		this.src = s;
		this.RCV_BUF = receiveBufferSize;
	}
	/*
	 * Run until end of stream or failure.  Then Exit
	 */
	public void run() {
		byte[] buffer = new byte[RCV_BUF];
			try {
				//http://stackoverflow.com/questions/5562370/how-to-identify-end-of-inputstream-in-java
				int bytesReadIn = in.read(buffer);
				//keep Reading till bytesReadIn is -1
				while (bytesReadIn != -1) {
					out.write(buffer, 0, bytesReadIn);
					bytesReadIn = in.read(buffer);
				}
				//bytesReadIn = -1.  So end of stream is reached.  Exit.
			}
			catch (IOException ie) {
				//Should we do anything here like PrintStackTrace()?  For now just Exit.
				//Should Logger know?
				System.out.println("[FwdThread IOException]: in " + src);
			}
			//let listener know connection is closed and the StreamSource
			listener.tcpConnectionClosed(src);
	}

}
