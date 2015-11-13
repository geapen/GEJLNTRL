import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Properties;

public class TCPProxy {
	private static final boolean DEBUG_ON = true;
	public static final String PROPERTIES_FILE = "config.properties";
	
	ArrayList<ServerTarget> serversList;

	
	//http://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
	public void ReadMyConfig() throws IOException{
		InputStream inputStream = null;
		try {
			Properties props = new Properties();
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 

			// get the property value and print it out
			String serverTargets = props.getProperty("serverTargets");
			String myPort = props.getProperty("myPort");

			serversList = new ArrayList<ServerTarget>();
			StringTokenizer stServers = new StringTokenizer(serverTargets,",");
			while (stServers.hasMoreTokens()) {
				String serverAndPort = stServers.nextToken();

			}



		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
	}
}
