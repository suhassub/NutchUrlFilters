package org.apache.nutch.urlfilter.exactdedup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.net.URLFilter;
import org.apache.nutch.plugin.Extension;
import org.apache.nutch.plugin.PluginRepository;
import org.apache.nutch.util.URLUtil;
import org.apache.nutch.util.domain.DomainSuffix;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.protocol.Protocol;
import org.apache.nutch.protocol.ProtocolFactory;
import org.apache.nutch.protocol.ProtocolOutput;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.hadoop.io.Text;

/**
 * <p>
 * Filters URLs based on a file that are exact duplicates with respect to the 
 * contents of already passed files .
 * </p>
 * 
 * <p>
 * The contents are checked, if there is a exact duplicate, then the url is 
 * disregarded.
 * 
 * <pre>
 * com apache.org www.apache.org
 * </pre>
 */

public class exactdedup implements URLFilter {

  private static final Logger LOG = LoggerFactory
      .getLogger(exactdedup.class);
   static HashMap<String, String> ExactDuplicates = new HashMap<String, String>();
  
  /**
   * Default constructor.
   */
   Configuration conf;
  public exactdedup() {
	}

 

  /**
   * Sets the configuration.
   */
  public void setConf(Configuration conf) {
    this.conf = conf;

    
  }

  public Configuration getConf() {
    return this.conf;
  }

  /**
    * filters thr url . if there is a duplicate, null is returned, else url 
    *is returned back
    */
  public String filter(String url) {

    try {
	
	//Fetch the content from the url.
    	//Get the contents from web
	ProtocolFactory factory = new ProtocolFactory(conf);
    	Protocol protocol = factory.getProtocol(url);
    	CrawlDatum datum = new CrawlDatum();
     	ProtocolOutput output = protocol.getProtocolOutput(new Text(url), datum);
	if (!output.getStatus().isSuccess()) {
      	System.out.println("Fetch failed with protocol status: "
          + output.getStatus());
      	return url;
	}
	Content content = output.getContent();
	if (content == null) {
      	System.out.println("No content for " + url);
      	return url;
    	}
	int temp, MinHash = -1;
	int MinHamm = 10000;
     	//MinHash stores the nearest duplicate's hash value
     	int HammDist;
	//File to maintain the hashes for next rounds
	File doc1 = new File("exactdup.txt");	
		FileWriter fw = new FileWriter(doc1,true);
		ArrayList<String> nDup = new  ArrayList<String>();
		int justCreated = -1;
		//If document doesnt exist
		if(!doc1.exists())
		{
			justCreated = 1;
			fw = new FileWriter(doc1,true);			
		}
	//set the filereader
	FileReader fr = new FileReader(doc1);
	BufferedReader br = new BufferedReader(fr);
	String line;
		while ((line = br.readLine()) != null) 
		{
			ExactDuplicates.put(line,line);
		}
	String str = new String(content.getContent());
	//Iterate through the words in the document.
	
	str=str.replaceAll("[\u0000-\u001f]","");
	str=str.replaceAll("\n","");
    	int i=0;
	String cipherText = "";
	//Pick every charcter which are mulitples of 5 and append it
	while(i<str.length())
	{
		cipherText+=str.charAt(i);
		i+=5;
	}
	//hash the ciphertext
	MessageDigest m=MessageDigest.getInstance("MD5");
	m.update(cipherText.getBytes(),0,cipherText.length());
	String hashString = new BigInteger(1,m.digest()).toString(16);
	Map.Entry hashes = null; 
	String hashToPutBack="";
	int matchFound = 0; //To test if match is found
	Iterator it = ExactDuplicates.entrySet().iterator();
	//loop through the list of hashes to find duplicate
	while(it.hasNext())
	{
		hashes = (Map.Entry)it.next();
		hashToPutBack = hashes.getKey().toString();
		if(hashToPutBack.equals(hashString))
		{
			matchFound = 1;
			break;
		}
			        	
			        	
	}
			        
	if(matchFound == 0) // No match found
	{
		ExactDuplicates.put(hashString, url );
		//Write the current URL's hash to the file.
		fw.write(hashString+"\n");
		
	}
	else //Match is found
	{
		System.out.println("Exact Duplicate Found ::");
		return null;
	}
	fw.close();
	fr.close();				
	return url;
    } catch (Exception e) {

      // if an error happens, allow the url to pass
      LOG.error("Could not apply filter on url: " + url + "\n"
          + org.apache.hadoop.util.StringUtils.stringifyException(e));
      return null;
    }
}
	
	
  
}
