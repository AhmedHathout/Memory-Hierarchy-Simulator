package cache;

import java.util.Arrays;
import misc.AdditionalMathFunctions;

public class Cache{
	
	/**
	 *  The total size of the cache (without the overheads)
	 */
	private short s;
	
	/**
	 * Number of BYTES in every line
	 */
	public short l;
	
	/**
	 *  Associativity
	 */
	private short m;
	
	/**
	 * Latency in terms of clock cycles
	 */
	private int latency;
	
	/**
	 * The lines of this cache
	 */
	private CacheLine[] lines; 
	
	/**
	 * Number of cache hits
	 */
	public int hits;
	
	/**
	 * Number of cache misses
	 */
	public int misses;
	
	/**
	 * Number of write through hits
	 */
	public int writeThroughHits;
	
	/**
	 * The cache address geometry
	 */
	public CacheAddress cacheAddress;
	
	/**
	 * 
	 * @param s			Cache size in BYTES
	 * @param l			Line size in BYTES
	 * @param m			Associativity
	 * @param latency	The latency in terms of clock cycles
	 */
	public Cache(short s, short l, short m, int latency) {
		
		this.s = s;
		this.l= l;
		this.m = m;
		this.latency = latency;
		
		this.cacheAddress= new CacheAddress(getNumberOfIndices(), l);
		
		lines = new CacheLine[getNumberOfLines()];
		intializeCacheLines();
		
		this.hits = this.misses = this.writeThroughHits = 0;
	}
	
	/**
	 * Returns the number of indices (number of sets)
	 * @return	The number of indices (number of sets)
	 */
	public short getNumberOfIndices() {
		return (short) (getNumberOfLines() / m);
	}

	/**
	 * Returns	the number of lines
	 * @return	the number of lines
	 */
	public short getNumberOfLines() {
		return (short) (s / l);
	}
	
	/**
	 * Initializes all the cache lines to be of size l
	 */
	public void intializeCacheLines() {
		for(int i = 0; i < lines.length; i++) {
			lines[i] = new CacheLine(l);
		}
	}
	
	/**
	 * Takes the address, decodes it then returns the whole line if it exists
	 * if not it returns null
	 * 
	 * @param address	The address of the data in the main memory
	 * 
	 * @return			An array of bytes that contains the data of the whole
	 * 					line or null if that line does not exist
	 */
	public byte[] readLine(short address) {
		cacheAddress.setAddress(address);
		
		short index = cacheAddress.getIndex();
		short tag = cacheAddress.getTag();
		CacheLine requiredLine = getLine(index, tag);
		
		if (requiredLine == null) {
			this.misses++;
			return null;
		}
		
		if (requiredLine.valid) {
			this.hits++;
			return requiredLine.readLine();
		}
		
		this.misses++;
		return null;
	}
	
	/**
	 * Takes an address and returns the word that has that specific address or
	 * null if there is no line containing that word
	 * 
	 * @param address	The address of the data in the main memory
	 * 
	 * @return			The required word or null if there is no line containing 
	 * 					that word
	 */
	public byte[] readWord(short address) {
		cacheAddress.setAddress(address);
		
		short index = cacheAddress.getIndex();
		short tag = cacheAddress.getTag();
		short displacement = cacheAddress.getDisplacement();
		
		CacheLine requiredLine = getLine(index, tag);
		
		if (requiredLine != null)
			return requiredLine.readWord(displacement);
		return null;
	}
	
	/**
	 * Replaces the old data in the line that contains the word that has the 
	 * given address with the data given from the previous cache (this method
	 * is called by reading from the memory hierarchy not writing in it)
	 * 
	 * @param address					The address of the data in the main 
	 * 									memory
	 * 
	 * @param data						the data given from the higher cache
	 * @param higherLevelDisplacement	The offset of the word in the higher
	 * 									cache
	 * @return							The old data that was in that line if
	 * 									it was dirty to be written in the next 
	 * 									level cache, otherwise null
	 */
	public byte[] writeLine(short address, byte[] data, short higherLevelDisplacement) {
		cacheAddress.setAddress(address);
		
		short index = cacheAddress.getIndex();
		short tag = cacheAddress.getTag();
		
		CacheLine requiredLine = getFirstEmptyLine(index);
		
		return requiredLine.writeLine(data, tag, higherLevelDisplacement);
	}
	
	/**
	 * Gets the first empty line in the cache. If there is no any, It returns
	 * a random one
	 * @param index	The index of the set
	 * @return		The required line
	 */
	private CacheLine getFirstEmptyLine(short index) {
		for (int j = 0; j < m; j++) {
			CacheLine currentLine = lines[index + j];
			if (!currentLine.valid)
				return currentLine;
		}
		
		int randomNumber = AdditionalMathFunctions.generateRandomNumber(index, 
				index + m);
		return lines[randomNumber];
	}

	/**
	 * Checks if there is a line in the set given by its index and has the tag
	 * tag
	 * 
	 * @param index	The index of the set
	 * @param tag	The tag of the required line
	 * @return
	 */
	public CacheLine getLine(short index, short tag) {
		for (int j = 0; j < m; j++) {
			if (lines[index + j].tag == tag && lines[index + j].valid)
				return lines[index + j];
		}
		
		return null;
	}
	
	/**
	 * @return The cycles spent to access this cache
	 */
	public int cyclesSpentToAccess() {
		return (hits - writeThroughHits) * latency;
	}
	
	/**
	 * @return how many times this cache has been accessed to read from or write
	 * to
	 */
	public int getTotalNumberOfAccesses() {
		return hits + misses;
	}
	
	public String toString() {
		String header = "Line index,Data\n";
		
		String data = "";
		for (int i = 0; i < this.getNumberOfLines(); i++) {
			CacheLine currentLine = this.lines[i];
			if (currentLine.valid)
				data += Integer.toHexString(i) + "," + Arrays.toString(currentLine.readLine()) + "\n";
		}
		
		String statistics = "Hits,Misses,Total number of accesses\n" + hits + 
				"," + misses + "," + getTotalNumberOfAccesses() + "\n";
		
		return header + data + statistics;
	}
	
}
