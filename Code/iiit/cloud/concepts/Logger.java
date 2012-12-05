package iiit.cloud.concepts;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;


public class Logger 
{
    public Logger() {}
    private static String mPrefix = "";
    public static void setPrefix(String pText)
    {
        if(pText.length()>0) mPrefix="["+pText+"]";
        else mPrefix = "";
    }
    public static void forceLog(String pText) 
    {
        log("[FORCE] " +  pText);
    }  
    public static void customLog(String pFileName, String pText) 
    {
        String oldFile = Catalog.LOG_FILE;
        Catalog.LOG_FILE = Catalog.STAGE_DIR + Catalog.PATH_SEPERATOR + pFileName;
        log("[" + Level.SEVERE.getName() + "] " + pText);
        Catalog.LOG_FILE = oldFile;
    }        
    public static void justLog(String pText) 
    {
        log("[" + Level.SEVERE.getName() + "] " + pText);
    }    
    public static void log(Level pLevel, String pText) 
    {
        if (pLevel.intValue() >= Catalog.LOG_LEVEL.intValue()) 
        {
           log("[" + pLevel.getName() + "] " + pText);
        }
    }
    public static void log(Level pLevel, Exception pExp) 
    {
        if (pLevel.intValue() >= Catalog.LOG_LEVEL.intValue()) 
        {
           StringWriter pMsg = new StringWriter();
           pExp.printStackTrace(new PrintWriter(pMsg));
           log("[" + pLevel.getName() + "] " +  pMsg.toString());
        }        
    }
    private static void log(String pText) 
    {
        try 
        {
            String lMsg = "\n" + mPrefix + "[" + new Date() + "]" + "[" + (new Date()).getTime() + "]" + pText;
            if(Catalog.LOG_SOP_ENABLED) System.out.println(lMsg);
            if (Catalog.LOG_FILE_ENABLED) 
            {
                BufferedWriter pWriter = new BufferedWriter(new FileWriter(Catalog.LOG_FILE, true));
                pWriter.write(lMsg);
                pWriter.close();
            }
        }
        catch (Exception lExp) {}
    }    
}