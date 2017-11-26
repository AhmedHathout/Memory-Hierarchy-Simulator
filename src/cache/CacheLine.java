package cache;

import java.lang.reflect.Array;
import java.util.Arrays;

public class CacheLine {
	
	/**
	 * The valid bit
	 */
	boolean valid;
	
	/**
	 * The tag
	 */
	short tag;
	
	/**
	 * Number of bytes in the line;
	 */
	int l;
	
	/**
	 * The data contained in this line which is array of bytes
	 */
	byte[] data;
	
	/**
	 * Main constructor
	 * 
	 * @param valid	The valid bit
	 * @param tag	The tag
	 * @param l		Number of bytes in the line
	 */
	CacheLine(boolean valid, short tag, int l) {
		
		this.valid = valid;
		this.tag = tag;
		this.l = l;
		
		this.data = new byte[l];
		
	}
	
	/**
	 * Shorthand constructor
	 * 
	 * @param l 	Number of bytes in the line
	 */
	CacheLine(int l) {
		this(false, (short) 0, l);
	}
	
	/**
	 * Returns an array of bytes containing the required data starting from displacement to displacement + 1
	 * @param displacement	The index of the first byte of the word
	 * @return	data
	 */
	byte[] readData(short displacement) {
		return Arrays.copyOfRange(this.data, displacement, displacement + 1);
	}
	
}
