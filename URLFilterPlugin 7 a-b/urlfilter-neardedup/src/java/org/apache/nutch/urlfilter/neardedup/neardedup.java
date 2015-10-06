package org.apache.nutch.urlfilter.neardedup;

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
 * Filters URLs based on a file that are near duplicates with respect to the 
 * contents of already passed files .
 * </p>
 * 
 * <p>
 * The contents are checked, if there is a near duplicate, then the url is 
 * disregarded.
 * 
 * <pre>
 * com apache.org www.apache.org
 * </pre>
 */

public class neardedup implements URLFilter {

  private static final Logger LOG = LoggerFactory
      .getLogger(neardedup.class);
   static HashMap<Integer, String> NearDuplicates = new HashMap<Integer, String>();
  
  /**
   * Default constructor.
   */
   Configuration conf;
  public neardedup() {
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
    * filters thr url . if there is a duplicate null is returned else url 
    *is returned back
    */
  public String filter(String url) {

    try {
	
	//Fetch the content from the url.
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
     	ArrayList<String> NearUrls = new ArrayList<String>();
     	int HammDist;
	File doc1 = new File("neardup.txt");	
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
			NearDuplicates.put(Integer.parseInt(line),line);
		}
	String str = new String(content.getContent());
	//Replace all special unicode characters
	str=str.replaceAll("[\u0000-\u001f]","");
	str=str.replaceAll("\n","");
	int hashvalue=CalcHash(str);
	//Iterate through the words in the document.
	Iterator it = NearDuplicates.entrySet().iterator();
	Map.Entry hashes; // Takes hashes from NearDuplicates one at a time
	while (it.hasNext()) {         	
		hashes = (Map.Entry)it.next();
		HammDist = HammingDistance(((Integer) hashes.getKey()).intValue(), hashvalue);
	//System.out.println(HammDist);
	if(HammDist<5 && HammDist >0)
	{
		if(HammDist<MinHamm)
		{
			MinHash = ((Integer) hashes.getKey()).intValue();
			MinHamm = HammDist;
		}
	}

	}
	
	if(MinHash == -1) //If no near duplicate found
	{
		NearDuplicates.put(hashvalue, url);
		fw.write(hashvalue+"\n");
	}
	else // If near duplicate found
	{
		System.out.println("Near Duplicate Found ::");
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
	
	public  int HammingDistance(int key, int finalHash1) {
		// TODO Auto-generated method stub
		int i = key ^ finalHash1;
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		i = (i + (i >>> 4)) & 0x0f0f0f0f;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		return i & 0x3f;
	}

	public int CalcHash(String s) throws Exception{
    	  //MessageDigest m=MessageDigest.getInstance("MurmurHash");
    	
    	  //long FinalHash;
    	  int[] V=new int[32];
          StringTokenizer st = new StringTokenizer(s);  
          String word ="";
          
          int wordHash;
	  //Calculate hash for each word
          while(st.hasMoreTokens())
          {
          	word = st.nextToken(); 
          	wordHash = hash32(word);
  
          	for(int i=32;i>=1;--i)
          	{
          		if (((wordHash >> (32 - i)) & 1) == 1)
					V[i - 1]+=1;
				else
					V[i - 1]-=1;
          	}
             
          }
          //getting the fingerprint          
          int Finalhash = 0x00000000;
  		  int one = 0x00000001;
	  		for (int i = 32; i >= 1; --i) {
	  			if (V[i - 1] > 0) {
	  				Finalhash |= one;
	  			}
	  			one = one << 1;
	  		}
          return Finalhash;
    }
	//Logic for Murumurhash encryption
	public  int hash32(String doc) throws Exception {
			byte[] buffer = doc.getBytes(Charset.forName("utf-8"));
			//ByteBuffer data = ByteBuffer.wrap(buffer);
			return hash32(buffer,  buffer.length, 0x9747b28c);
			//return hash321(doc);
		}

		public  int hash32(final byte[] data, int length, int seed) {
			 final int m = 0x5bd1e995;
		        final int r = 24;

		        // Initialize the hash to a random value
		        int h = seed^length;
		        int length4 = length/4;

		        for (int i=0; i<length4; i++) {
		            final int i4 = i*4;
		            int k = (data[i4+0]&0xff) +((data[i4+1]&0xff)<<8)
		                    +((data[i4+2]&0xff)<<16) +((data[i4+3]&0xff)<<24);
		            k *= m;
		            k ^= k >>> r;
		            k *= m;
		            h *= m;
		            h ^= k;
		        }
		        
		        // Handle the last few bytes of the input array
		        switch (length%4) {
		        case 3: h ^= (data[(length&~3) +2]&0xff) << 16;
		        case 2: h ^= (data[(length&~3) +1]&0xff) << 8;
		        case 1: h ^= (data[length&~3]&0xff);
		                h *= m;
		        }

		        h ^= h >>> 13;
		        h *= m;
		        h ^= h >>> 15;

		        return h;
		}
  
}
