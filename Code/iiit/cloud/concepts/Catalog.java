package iiit.cloud.concepts;
import java.util.logging.Level;

public class Catalog 
{
    public Catalog() {}

    // Directory Settings
    public static final String PATH_SEPERATOR       = System.getProperty("file.separator");
    //public static String STAGE_DIR                  = "/tmp" + PATH_SEPERATOR;           
    public static String STAGE_DIR                  = "/media/sf_windows/Phase3" + PATH_SEPERATOR;
    public static String LOG_FILE                   = STAGE_DIR + "debug.log";    

    // Log Settings    
    public static final Level LOG_LEVEL    = Level.INFO;    
    public static Boolean LOG_SOP_ENABLED  = true;
    public static Boolean LOG_FILE_ENABLED = true;    
}
