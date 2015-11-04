
import java.io.*;
/**
 *
 * @author James Bossingham
 */
class CLTFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".clt");
    }
    
    public String getDescription() {
        return ".clt files";
    }
}
