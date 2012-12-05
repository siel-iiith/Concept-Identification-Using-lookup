package iiit.cloud.concepts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;


public class Seed
{
    public static void main(String[] args) throws Exception
    {        
        List<String> lines = Files.readLines(new File(args[0]), Charsets.UTF_8);
        List<ServerAddress> servers = new ArrayList<ServerAddress>();
        String dataset = "";
        int i=0;
        for (String line : lines) 
        {
            line = line.trim();
            if(line.length()==0 || line.startsWith("#")) continue;
            if(i++==0) {dataset = line; continue;}
            servers.add(new ServerAddress(line));
        }        
        
        Mongo mongo = new Mongo(servers);
        mongo.setWriteConcern(WriteConcern.SAFE);
        DB database = mongo.getDB("Cloud");
        database.dropDatabase();
        database = mongo.getDB("Cloud");
        DBCollection concepts = database.getCollection("Concepts");
        
        InputStream    fis;
        BufferedReader br;
        String         line;

        Logger.justLog("Loading starts...");
        fis = new FileInputStream(dataset);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        i=0;
        while ((line = br.readLine()) != null) 
        {
            try {
				BasicDBObject concept = new BasicDBObject();
				//line = line.replaceAll("([.,!?:;'\"-]|\\s)+", " ");
				//concept.put("name", line.trim().toLowerCase());
				//concept.put("id", ++i);            
				String fields[] = line.split(";", 2);
				concept.put("name", fields[1].trim().toLowerCase().replaceAll("([.,!?:;'\"-]|\\s)+", " "));
				concept.put("id", fields[0]);
				concepts.insert(concept);
				Logger.justLog("Success => " + ++i);
			} catch (Exception e) {
				Logger.justLog("Exception => " + e.getMessage() + " => " + line);
			}
        }
        Logger.justLog("Loading done...");
        
//        BasicDBObject query = new BasicDBObject();
//        //query.put("name", "test");
//        DBCursor cursor = concepts.find(query);
//        try 
//        {
//            while(cursor.hasNext()) 
//            {
//                System.out.println(cursor.next());
//            }
//        } 
//        finally 
//        { 
//            cursor.close();
//        }        
    }
}
