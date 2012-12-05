package iiit.cloud.concepts;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class Lookup 
{
	//private static final Log LOG = LogFactoryImpl.getLog(Identify.class);
	public static void main(String[] args) throws Exception
    {
        try 
        {
        	// Submit Job
            {
                Configuration conf = new Configuration();
                conf.set("db.server", "localhost");    
                conf.set("db.port", "27017");   
        
                Path in = new Path("cloud/identify/url/in");
                Path out = new Path("cloud/identify/url/out");
    
                FileSystem fs = FileSystem.get(conf);
                if (fs.exists(out))
                    fs.delete(out, true);
                if (fs.exists(in))
                    fs.delete(in, true);
    
                String srcPath = "/smadhapp/docs";
                Collection<File> documents = FileUtils.listFiles(new File(srcPath), null, true);
                for (File document : documents) 
                {
                    String relPath = document.getPath().replaceAll(srcPath,"");
                    fs.copyFromLocalFile(true, true, new Path(document.getAbsolutePath()) , new Path(in + Catalog.PATH_SEPERATOR + relPath));
                }             
                
                Job job = new Job(conf);
                job.setNumReduceTasks(0);
                job.setJobName("Identify Concepts");
                job.setMapperClass(Map.class);
                job.setJarByClass(Map.class);
                
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                
                job.setInputFormatClass(TextInputFormat.class);
                job.setOutputFormatClass(SequenceFileOutputFormat.class);
                    
                FileInputFormat.addInputPath(job, in);
                FileOutputFormat.setOutputPath(job, out);
                    
                job.waitForCompletion(true);                      
            }
            
            // Reader Job Output
            int i=0;
            while(true)
            {
                Configuration conf = new Configuration();
                FileSystem fs = FileSystem.get(conf);
                
                Path path = new Path("cloud/identify/url/out" + Catalog.PATH_SEPERATOR +"/part-m-0000"+i++);
                try
                {
	                SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
	                Text doc = new Text();
	                Text concept = new Text();
	                while (reader.next(doc, concept))
	                {
	                    //Logger.customLog("output.csv", doc + " => " + concept);
	                	//System.out.println("[OUTPUT] " + doc + " => " + concept);
	                	Logger.justLog("[OUTPUT] " + doc + " => " + concept);
	                }
	                reader.close();   
                }
                catch(FileNotFoundException e) {break;}
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace(); 
        }
    }
}
