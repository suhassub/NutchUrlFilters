package exactduplicates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class exactduplicates {

	
	public static HashMap<String, String> ReadLines(String FileName) throws Exception
	{
		HashMap<String, String> ExactDuplicates = new HashMap<String, String>();
		File doc1 = new File("dump");
		FileReader fr1 = new FileReader(doc1);
		BufferedReader b1 = new BufferedReader(fr1);
		String line1;
		String url=null;
		//read line from the file
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
			{//Get the parse text
				StringBuilder sb= new StringBuilder();
				int flag=0;
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
					int i=0;
					String cipherText = "";
					//Get the charcter at different position in multiples of 5
					while(i<sb.toString().length())
					{
						cipherText+=sb.toString().charAt(i);
						i+=5;
					}
					
					MessageDigest m=MessageDigest.getInstance("MD5");
			        m.update(cipherText.getBytes(),0,cipherText.length());
			        String hashString = new BigInteger(1,m.digest()).toString(16);
			        
			        //Check if hash already exists in ExactDuplicates
			        Map.Entry hashes = null; //Represents exact hash match if any
			        String hashToPutBack="";
			        int matchFound = 0; //To test if match is found
			        Iterator it = ExactDuplicates.entrySet().iterator();
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

			        }
			        
			        else //Match is found
			        {
			        	System.out.println(url);
			        }
			        
			        
				}
			}
		}
		return ExactDuplicates;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ReadLines("dump");
		
		
		
	}

}