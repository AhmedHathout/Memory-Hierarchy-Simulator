package cache;

import misc.*;

public class CacheAddress {
	
	/**
	 * NUMBER of tag BITS
	 */
	byte t;
	
	/**
	 * NUMBER of index BITS
	 */
	byte i;
	
	/**
	 * NUMBER of displacement BITS
	 */
	byte d;
	
	/**
	 * The address of the data in the main memory
	 */
	private short address;
	
	/**
	 * @param numberOfIndices	The number of indices (sets)
	 * @param l					Number of bytes in every line;
	 */
	public CacheAddress(short numberOfIndices, short l) {
		this.address = 0;
		this.i = (byte) AdditionalMathFunctions.log2(numberOfIndices - 1);
		this.d = (byte) AdditionalMathFunctions.log2(l - 1);
		this.t = (byte) (16 - i - d);
	}
	
	/**
	 * the address of this cache address
	 * @param address	The address of the word in the main memory
	 */
	public void setAddress(short address) {
		this.address = address;
	}
	
	/**
	 * Returns the displacement (offset) of the required data
	 * @return displacement
	 */
	public short getDisplacement() {
		short setBits = (short) (Math.pow(2, d) - 1);
		return (short) (address & setBits);
	}
	
	/**
	 * Returns the index of the set of the required data 
	 * @return index
	 */
	public short getIndex() {
		short setBits = (short) (Math.pow(2, i) - 1);
		return (short) ((address >> d) & setBits);
	}
	
	/**
	 * Returns the tag of the required data
	 * @return tag
	 */
	public short getTag() {
		short setBits = (short) (Math.pow(2, t) - 1);
		return (short) ((address >> (i + d)) & setBits);
	}
	
}
