package iiit.cloud.concepts;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class Map extends Mapper<LongWritable, Text, Text, Text>
{
    Text word = new Text();
    Text fileName = new Text();
    DBCollection concepts = null; 
    BasicDBObject query = new BasicDBObject();
    Stack<String> stack = new Stack<String>();
    ArrayList<String> currTokenList = new ArrayList<String>();
    HashMap<String, Integer> mCache = new HashMap<String, Integer>();
    Integer mOne = new Integer(1);
    
    public void setup(Context context) throws InterruptedException, IOException 
    {
        super.setup(context);

        String fName = ((FileSplit)context.getInputSplit()).getPath().toString();
        fileName.set(fName);

        Configuration conf = context.getConfiguration();
		String dbserver = conf.get("db.server");
		String dbport = conf.get("db.port");	    	
    	//Mongo mongo = new Mongo(Arrays.asList(new ServerAddress(pDBServer1, Integer.getInteger(pDBPort1))));
		Mongo mongo = new Mongo(dbserver);
        DB database = mongo.getDB("Cloud");
        concepts = database.getCollection("Concepts");          
    }    
    
    public void map(LongWritable key, Text value, Context context)
    {
		//Logger.justLog("Map started...");
		Date start = new Date();
        String[] tokens = value.toString().toLowerCase().split("([.,!?:;'\"-]|\\s)+");
		int noOfConcepts = 0;
		int avgConceptSize = 0;
    	try 
    	{
    		Configuration conf = context.getConfiguration();		
			int idx;
            String currConcept; 
            for (idx=tokens.length-1; idx>=0; idx--)
                stack.push(tokens[idx]);
            //Logger.justLog("Stack => " + stack);
            while (!stack.isEmpty()) 
            {
                idx=0;
                currTokenList.clear();
                while (idx++<7 && !stack.isEmpty()) 
                	currTokenList.add(stack.pop());
                while(!currTokenList.isEmpty()) 
                {
                	//Logger.justLog("currTokenList => " + currTokenList);
                	currConcept = "";
                    for (String token : currTokenList) currConcept += token + " ";
                    currConcept = currConcept.trim();
                    //Logger.justLog("findConcept => [" + findConcept + "]");
                    if(mCache.containsKey(currConcept) || findConcept(currConcept))
                    {
                        //Logger.justLog("Found => " + currConcept);
                    	word.set(currConcept);
                    	mCache.put(currConcept, mOne);
                    	context.write(fileName, word);
                    	noOfConcepts++;
                    	avgConceptSize += currTokenList.size();
                        break;
                    }
                    else
                    {
                        if(currTokenList.size()>1) stack.push(currTokenList.get(currTokenList.size()-1));   
                        currTokenList.remove(currTokenList.size()-1);
                        //Logger.justLog("Stack => " + stack);
                    }
                }
                //Logger.justLog("Stack => " + stack);
            }			
		} 
    	catch (Exception e) 
		{
    		Logger.log(Level.SEVERE, e);
		}
    	Date end = new Date();
		//Logger.justLog("Map completed...");
    	if(noOfConcepts>0) avgConceptSize/=noOfConcepts;
    	else avgConceptSize=0;
    	Logger.justLog(fileName+ "," + tokens.length + "," + noOfConcepts + "," + avgConceptSize + "," + (end.getTime()-start.getTime()));
    }

    public boolean findConcept(String pConcept)
    {
    	boolean lRet =  false;	
        query.put("name", pConcept.trim());
        //Logger.justLog("Query => " + pConcept);
        DBCursor cursor = concepts.find(query);
    	if (cursor.count() > 0)  lRet = true;  
        //Logger.justLog("Found => " + lRet);    	
    	cursor.close();   
        return lRet;
    }
}
