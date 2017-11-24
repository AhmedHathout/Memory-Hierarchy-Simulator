package cache;

public class CacheLine {
	
	/**
	 * The valid bit
	 */
	boolean valid;
	
	/**
	 * The tag
	 */
	int tag;
	
	/**
	 * Number of bits in every line;
	 */
	int l;
	
	/**
	 * The data contained in this line which is array of bytes
	 */
	byte[] data;
	
	
	/**
	 * 
	 * @param valid
	 * @param tag
	 * @param data
	 */
	CacheLine(boolean valid, int tag, int l) {
		
		this.valid = valid;
		this.tag = tag;
		this.l = l;
		
		this.data = new byte[l];
		
	}
	
}
