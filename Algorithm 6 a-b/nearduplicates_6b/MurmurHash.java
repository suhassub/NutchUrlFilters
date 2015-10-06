package nearduplicates;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class MurmurHash {
	

		public static int hash32(String doc) throws Exception {
			byte[] buffer = doc.getBytes(Charset.forName("utf-8"));
			//ByteBuffer data = ByteBuffer.wrap(buffer);
			return hash32(buffer,  buffer.length, 0x9747b28c);
			//return hash321(doc);
		}

		public static int hash32(final byte[] data, int length, int seed) {
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
		
				
		public static int hash321(String word) throws Exception
		{
			//String word="this is a line";
			char [] key = word.toCharArray();
			int len=key.length;
			int c1 = 0xcc9e2d51;
			int c2 = 0x1b873593;
			int r1=15;
			int r2=3;
			int m=5;
			int n=0xe6546b64;
			
			int hash=0x774fed01;//insert seed here! random seed per application
			
			int nb=len/4;
			char[] blocks=key;
			
			for(int i =0;i<nb;i++)
			{
				int k =blocks[i];
				k*=c1;
				k=(k<<r1)|(k>>(32-r1));
				k*=c2;
				
				hash^=k;
				hash =(hash<<r2)|(hash>>(32-r2))*m +n;
				
				
			}
			
			int lenm=nb*4;
			int left = len-lenm;
			int k1=0;
			if(left !=0)
			{
				if(left >=3)
				{
					k1^=key[lenm+3-1]<<16;
					
				}
				if(left>=2)
				{
					k1^=key[lenm+2-1]<<8;
					
				}
				if(left>=1)
				{
					k1^=key[lenm+1-1];
					
				}
				
				k1*=c1;
				k1=(k1<<r1)	|(k1>>(32-r1));
				k1*=c2;
				hash^=k1;
				
			}
			
			hash^=len;
			hash ^= (hash >> 16);
			hash *= 0x85ebca6b;
			hash ^= (hash >> 13);
			hash *= 0xc2b2ae35;
			hash ^= (hash >> 16);
			
			return (hash);
			
			
		}
		
		
}