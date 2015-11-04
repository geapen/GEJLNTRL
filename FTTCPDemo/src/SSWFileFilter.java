
import java.io.*;
/**
 *
 * @author James Bossingham
 */
class SSWFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".ssw");
    }
    
    public String getDescription() {
        return ".SSW files";
    }
}