package cache;

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
	short l;
	
	/**
	 * The data contained in this line which is array of bytes
	 */
	byte[] data;
	
	/**
	 * The dirty bit used in write back
	 */
	public boolean dirty;
	
	/**
	 * Main constructor
	 * 
	 * @param valid	The valid bit
	 * @param tag	The tag
	 * @param l		Number of bytes in the line
	 */
	CacheLine(boolean valid, short tag, short l) {
		
		this.valid = valid;
		this.tag = tag;
		this.l = l;
		this.dirty = false;
		
		this.data = new byte[l];
		
	}
	
	/**
	 * Shorthand constructor
	 * 
	 * @param l 	Number of bytes in the line
	 */
	CacheLine(short l) {
		this(false, (short) 0, l);
	}
	
	/**
	 * @return a 1D array of bytes that contains the data of that line
	 */
	byte[] readLine() {
		return this.data.clone();
	}
	
	/**
	 * Returns an array of bytes containing the required data starting from displacement to displacement + 1
	 * @param displacement	The index of the first byte of the word
	 * @return	data
	 */
	public byte[] readWord(short displacement) {
		return Arrays.copyOfRange(this.data, displacement, displacement + 2);
	}
	
	public byte[] writeLine(byte[] data, short tag, short HigherLevelDisplacement) {
		this.valid = true;
		this.tag = tag;
		
		byte[] oldData = null;
		
		int thisLineOffset = HigherLevelDisplacement / this.l;
		
		if (this.dirty) {
			oldData = new byte[this.data.length];
			System.arraycopy(this.data, 0, oldData, 0, this.data.length);
			this.dirty = false;
		}
		
		System.arraycopy(data, thisLineOffset * this.l, this.data, 0, this.data.length);
		return oldData;
	}
	
	/**
	 * Writes regData that is in displacement displacement
	 * @param displacement	The displacement of the word in the line
	 * @param regData		The data from the register
	 */
	public void writeWord(short displacement, short regData) {
		this.dirty = true;
		byte rightByte = (byte) regData;
		byte leftByte = (byte) (regData >> 8);
		this.data[displacement] = rightByte;
		this.data[displacement + 1] = leftByte;
	}
	
}
