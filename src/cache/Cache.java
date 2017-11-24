package cache;
public class Cache{
	
	/**
	 *  The total size of the cache (without the overheads)
	 */
	private int s;
	
	/**
	 * Number of bits in every line;
	 */
	private int l;
	
	/**
	 *  Associativity
	 */
	private int m;
	
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
	public Cache(int s, int l, int m, int latency) {
		
		this.s = s;
		this.l= l;
		this.m = m;
		this.latency = latency;
		
		lines = new CacheLine[getNumberOfLines()];
		
	}
	
	/**
	 * Returns i
	 * @return i
	 */
	public int getNumberOfLines() {
		return s / l;
	}
	
}
