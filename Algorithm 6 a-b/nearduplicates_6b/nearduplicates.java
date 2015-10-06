package nearduplicates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import nearduplicates.MurmurHash;

public class nearduplicates {

	public static HashMap<Integer, String> ReadLines(String FileName) throws Exception
	{
		HashMap<Integer, String> NearDuplicates = new HashMap<Integer, String>();
		File doc1 = new File("dump");
		FileReader fr1 = new FileReader(doc1);
		BufferedReader b1 = new BufferedReader(fr1);
		StringBuffer stringBuffer1 = new StringBuffer();
		String line1;
		//Read from the file
		String url=null;
		while ((line1 = b1.readLine()) != null) {
			String mysz2 = line1.replaceAll("\\s","");
			String[] line2=mysz2.split("::");
			if(line2[0].equals(""))
			{
				continue;
			}
			else if(line2[0].equals("Recno"))
			{
				continue;
			}
			else if(line2[0].equals("URL"))
			{
				if(!line2[1].equals("") && !line2.equals(null))
				{
					url=line2[1].toString();
				}
			}
			else if(line2[0].equals("ParseText"))
			{
				StringBuilder sb= new StringBuilder();
				int flag=0;
				// Read the parsetext
				while((line1=b1.readLine())!=null)
				{
					if(line1.contains("Recno::")|| line1==null)
					{
						break;
					}
					if(line1!=null && !line1.equals("\n") && !line1.equals(""))
					{
						flag=1;
						sb.append(line1.toString());
					}
					
				}
				if((line1!=null && line1!="" && line1.contains("Recno::") && flag==1))
				{
				int temp, MinHash = -1;
		     	int MinHamm = 10000;
		     	//MinHash stores the nearest duplicate's hash value
		     	
		     	ArrayList<String> NearUrls = new ArrayList<String>();
		     	int HammDist;
				int hashvalue=CalcHash(sb.toString());
				Iterator it = NearDuplicates.entrySet().iterator();
		     	Map.Entry hashes; // Takes hashes from NearDuplicates one at a time
		         while (it.hasNext()) {         	
		        	 
		             hashes = (Map.Entry)it.next();
		             HammDist = HammingDistance(((Integer) hashes.getKey()).intValue(), hashvalue);
		             //System.out.println(HammDist);
		             if(HammDist<3 && HammDist >0)
		             {
		             	if(HammDist<MinHamm)
		             	{
		             		MinHash = ((Integer) hashes.getKey()).intValue();
		             		MinHamm = HammDist;
		             	}
		             		
		             }
		             
//		             it.remove(); //Avoids a ConcurrentModificationException
		         }

		         if(MinHash == -1) //If no near duplicate found
		         {
		         	NearDuplicates.put(hashvalue, url);

		         }
		         else // If near duplicate found
		         {
		        	 System.out.println(url);
		         	
		         }
				}
		             
			}
		}
		
		return null;
		
	}
	
	static HashMap<String, Integer> listc=new HashMap<String, Integer>();
    @SuppressWarnings("rawtypes")
	public static void main(String args[]) throws Exception, NullPointerException{
     
		String s=null,s2=null;
				
		ReadLines("dump");
       
             

//Test Ends
           

    }
    
	private static int HammingDistance(int key, int finalHash1) {
		// TODO Auto-generated method stub
		int i = key ^ finalHash1;
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		i = (i + (i >>> 4)) & 0x0f0f0f0f;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		return i & 0x3f;
	}

	public static int CalcHash(String s) throws Exception{
    	  //MessageDigest m=MessageDigest.getInstance("MurmurHash");
    	
    	  //long FinalHash;
    	  int[] V=new int[32];
          StringTokenizer st = new StringTokenizer(s);  
          String word ="";
          
          int wordHash;
          while(st.hasMoreTokens())
          {
          	word = st.nextToken(); 
          	wordHash = MurmurHash.hash32(word);
  
          	for(int i=32;i>=1;--i)
          	{

          		if (((wordHash >> (32 - i)) & 1) == 1)
					V[i - 1]+=1;
				else
					V[i - 1]-=1;
          	}
              
              
          }
                    
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

}
