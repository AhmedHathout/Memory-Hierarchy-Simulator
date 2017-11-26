package cache;

import org.omg.CORBA.INITIALIZE;

public class Cache{
	
	/**
	 *  The total size of the cache (without the overheads)
	 */
	private short s;
	
	/**
	 * Number of bytes in every line;
	 */
	private short l;
	
	/**
	 *  Associativity
	 */
	private short m;
	
	/**
	 * Latency in terms of clock cycles
	 */
	private int latency;
	
	/**
	 * The data that contained in the cache
	 */
	private CacheLine[] lines; 
	
	/**
	 * 
	 * @param s
	 * @param l
	 * @param m
	 * @param latency
	 */
	public Cache(short s, byte l, short m, int latency) {
		
		this.s = s;
		this.l= l;
		this.m = m;
		this.latency = latency;
		
		lines = new CacheLine[getNumberOfLines()];
		
	}
	
	/**
	 * Returns the number of lines
	 * @return
	 */
	public short getNumberOfLines() {
		return (short) (s / l);
	}
	
	/**
	 * Returns the number of indices (sets)
	 * @return
	 */
	public short getNumberOfIndices() {
		return (short) (getNumberOfLines() / m);
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
	 * 
	 * @param address	The address of the data in the main memory
	 * @return   An array of bytes of size 2 that contains the data or null if not found
	 */
	
	public byte[] readData(short address) {
		CacheAddress cacheAddress= new CacheAddress(address, getNumberOfIndices(), l);
		
		int index = cacheAddress.getIndex();
		int tag = cacheAddress.getTag();
		
		for (int j = 0; j < m; j++) {
			CacheLine currentLine = lines[index + j];
			if (currentLine.tag == tag && currentLine.valid)
				return currentLine.readData(cacheAddress.getDisplacement());
		}
		
		return null;
	}
	
}
