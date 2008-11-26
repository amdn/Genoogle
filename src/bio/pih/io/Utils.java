package bio.pih.io;

import java.nio.ByteBuffer;

import bio.pih.io.proto.Io.StoredSequence;

import com.google.protobuf.ByteString;

/**
 * Some methods for various proposes.
 * 
 * @author albrecht
 */
public class Utils {
	public static int[] getEncodedSequence(StoredSequence storedSequence) {
		ByteString encodedSequence = storedSequence.getEncodedSequence();
		byte[] byteArray = encodedSequence.toByteArray();
		final int[] ret = new int[byteArray.length/4];
		ByteBuffer.wrap(byteArray).asIntBuffer().get(ret);
		return ret;
	}
	

	public static String invert (String s) {
	     StringBuilder temp = new StringBuilder();
	     for (int i=s.length()-1; i>=0; i--) {
	    	 temp.append(s.charAt(i));
	     }
	     return temp.toString();
	}
	
	public static String sequenceComplement(String seqString) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seqString.length(); i++) {
			char base = seqString.charAt(i);
			char complement = Utils.getComplement(base);
			sb.append(complement);
		}
		return sb.toString();
	}
	
	public static char getComplement(char base) {
		switch (base) {
		case 'A': return 'T';
		case 'T': return 'A';
		case 'C': return 'G';
		case 'G': return 'C';
		case 'a': return 't';
		case 't': return 'a';
		case 'c': return 'g';
		case 'g': return 'c';
		default: throw new IllegalStateException(base + " is not a valid DNA base.");		
		}
	}
	
}
