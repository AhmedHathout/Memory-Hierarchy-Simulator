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
	short address;
	
	/**
	 * 
	 * @param address 			The address of the data in the main memory
	 * @param numberOfIndices	The number of indices (sets)
	 * @param l					Number of bytes in every line;
	 */
	CacheAddress(short address, short numberOfIndices, short l) {
		this.address = address;
		this.i = (byte) AdditionalMathFunctions.log2(numberOfIndices);
		this.d = (byte) AdditionalMathFunctions.log2(l);
		this.t = (byte) (16 - i - d);
	}
	
	/**
	 * Returns the displacement (offset) of the required data
	 * @return displacement
	 */
	short getDisplacement() {
		short setBits = (short) (Math.pow(2, d) - 1);
		return (short) (address & setBits);
	}
	
	/**
	 * Returns the index of the set of the required data 
	 * @return index
	 */
	short getIndex() {
		short setBits = (short) ((short) (Math.pow(2, i) - 1) << d);
		return (short) (address & setBits);
	}
	
	/**
	 * Returns the tag of the required data
	 * @return tag
	 */
	short getTag() {
		short setBits = (short) ((short) (Math.pow(2, t) - 1) << (d + i));
		return (short) (address & setBits);
	}
	
}
