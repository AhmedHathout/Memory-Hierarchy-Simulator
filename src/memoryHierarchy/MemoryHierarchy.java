package memoryHierarchy;

import assembler.Assembler;
import memoryHierarchy.cache.Cache;
import memoryHierarchy.cache.CacheLine;
import memoryHierarchy.mainMemory.MainMemory;

public class MemoryHierarchy {
	
	/**
	 * Level 1 Instruction Cache
	 */
	private Cache instructionCacheL1;
	
	/**
	 * Level 1 Data Cache
	 */
	private Cache dataCacheL1;
	
	/**
	 * Higher level cahces
	 */
	private Cache[] caches;
	
	/**
	 * Main memory
	 */
	public MainMemory mainMemory;
	
	/**
	 * Writing policy if hit
	 */
	private String hitWritingPolicy;
	
	/**
	 * Writing policy if miss
	 */
	private String missWritingPolicy;
	
	/**
	 * initializes the caches and memory
	 * 
	 * @param SLM				A 2D array containing the cache geometry for every
	 * 							level
	 * 
	 * @param latencies			A 1D array containing the latency of every cache 
	 * 							level and the memory 
	 *
	 * @param initialData		The initial data that is to be loaded to the 
	 * 							memory
	 * 
	 * @param locations			The location of the initial data data
	 * @param hitWritingPolicy	The writing policy if hit
	 * @param missWritingPolicy The writing policy if missed
	 */
	public MemoryHierarchy(int[][] SLM, int[] latencies, String[] initialData, 
			String[] locations, String hitWritingPolicy, String missWritingPolicy) {
		int numberOfAdditionalLevels = SLM.length - 1;
		
		caches = new Cache[numberOfAdditionalLevels];
		initializeCaches(SLM, latencies);
		
		mainMemory = new MainMemory(latencies[latencies.length - 1], 
				initialData, locations);
		
		this.hitWritingPolicy = hitWritingPolicy;
		this.missWritingPolicy = missWritingPolicy;
	}
	
	/**
	 * Initializes all caches
	 * 
	 * @param SLM			A 2D array containing the cache geometry for every level
	 * @param latencies		A 1D array containing the latency of every cache level
	 * 						and the memory 
	 */
	private void initializeCaches(int[][] SLM, int[] latencies) {
		instructionCacheL1 = new Cache((short)(SLM[0][0]), (byte)(SLM[0][1]), (short)(SLM[0][2]), latencies[0]);
		dataCacheL1 = new Cache((short)(SLM[0][0]), (byte)(SLM[0][1]), (short)(SLM[0][2]), latencies[0]);
		
		for(int i = 0; i < caches.length; i++) {
			caches[i] = new Cache((short)(SLM[i + 1][0]), (byte)(SLM[i + 1][1]), (short)(SLM[i + 1][2]), latencies[i + 1]);
		}
	}
	
	/**
	 * Reads the addressed word by address from the memories
	 * 
	 * @param address			The address of the required word
	 * @param isInstruction		specifies if that word is an instruction or 
	 * 							normal data for the program
	 * 
	 * @return					The required word
	 */
	public byte[] read(short address, boolean isInstruction) {
		for(int i = -1; i < caches.length; i++) {
			Cache currentCache;
			
			if (i == -1) 
				if (isInstruction)
					currentCache = instructionCacheL1;
				
				else
					currentCache = dataCacheL1;
			
			else
				currentCache = caches[i];
			
			byte[] data = currentCache.readLine(address);
			
			if (data != null)
				if (currentCache == instructionCacheL1 || currentCache == dataCacheL1)
					return currentCache.readWord(address);
				else
					return writeInPreviousCaches(i - 1, address, data, isInstruction);
				
		}
		
		Cache highestCache = caches[caches.length - 1];
		highestCache.cacheAddress.setAddress(address);
		short highestCacheDisplacement = highestCache.cacheAddress.getDisplacement();
		
		byte[] data = mainMemory.load(address, caches[caches.length - 1].l, highestCacheDisplacement);
		return writeInPreviousCaches(caches.length - 1, address, data, isInstruction);
	}
	
	/**
	 * Writes the data in all the lower caches
	 * 
	 * @param cacheLevelIndex	The highest cache that missed the previous load
	 * @param address			The address of the word 
	 * @param data				The data that is to be written
	 * @param isInstruction		specifies if that word is an instruction or 
	 * 							normal data for the program
	 * 
	 * @return					The required word after writing in all caches
	 */
	private byte[] writeInPreviousCaches(int cacheLevelIndex, short address, 
			byte[] data, boolean isInstruction) {
		
		Cache currentCache;
		Cache higherCache;
		int higherLevelIndex = cacheLevelIndex + 1;
		
		for (int j = cacheLevelIndex; j >= -1; j--) {
			
			if (higherLevelIndex == caches.length)
				higherCache = caches[cacheLevelIndex];
			else
				higherCache = caches[higherLevelIndex--];
			
			higherCache.cacheAddress.setAddress(address);
			short higherLevelDisplacement = higherCache.cacheAddress.getDisplacement();
			
			if (j == -1) 
				if (isInstruction)
					currentCache = instructionCacheL1;
				
				else
					currentCache = dataCacheL1;
			
			else
				currentCache = caches[j];
			
			byte[] oldData = currentCache.writeLine(address, data, higherLevelDisplacement);
			
			if (oldData != null) {
				boolean writeInMemory = true;
				
				for (int k = j + 1; k < caches.length; k++) {
					currentCache = caches[k];
					
					oldData = currentCache.writeLine(address, oldData, higherLevelDisplacement);
					
					if (oldData == null) {
						writeInMemory = false;
						break;
					}
				}
				
				if (writeInMemory) {
					Cache highestCache = caches[caches.length - 1];
					highestCache.cacheAddress.setAddress(address);
					short highestCacheDisplacement = highestCache.cacheAddress.getDisplacement();
					
					this.mainMemory.storeLine(address, oldData, highestCacheDisplacement);
				}
			}
		}
		
		if (isInstruction)
			return instructionCacheL1.readWord(address);
		else
			return dataCacheL1.readWord(address);
	}
	
	/**
	 * Writes a the data given by a register in the specified address
	 * 
	 * @param address	The address where the data should be written
	 * @param regData	The data itself :)
	 */
	public void write(short address, short regData) {
		
		boolean writeInMemory = false;
		
		for (int i = -1; i < caches.length; i++) {
			Cache currentCache;
			
			if(i == -1) {
				currentCache = dataCacheL1;
			}
			
			else {
				currentCache = caches[i];
			}
			
			currentCache.cacheAddress.setAddress(address);
			short index = currentCache.cacheAddress.getIndex();
			short tag = currentCache.cacheAddress.getTag();
			short displacement = currentCache.cacheAddress.getDisplacement();
			
			CacheLine requiredLine = currentCache.getLine(index, tag);
			
			if (requiredLine == null) { /* Miss */
				if (missWritingPolicy.equals("Write Allocate")) {
					
					// current cache misses is incremented in read
					read(address, false);
					i--;
				}
				
				else if (missWritingPolicy.equals("Write Around")) {
					currentCache.misses++;
					writeInMemory = true;
					continue;
				}
				
				else {
					throw new IllegalArgumentException("You must select either "
							+ "Write Allocate or Write Around: " + 
							missWritingPolicy);
				}
			}
			
			else { /* Hit */
				
				currentCache.hits++;
				if (hitWritingPolicy.equals("Write Through")) {
					currentCache.writeThroughHits++;
					requiredLine.writeWord(displacement, regData);
					writeInMemory = true;
				}
				
				else if (hitWritingPolicy.equals("Write Back")){
					requiredLine.writeWord(displacement, regData);
					writeInMemory = false;
					return;
				}
				
				else {
					throw new IllegalArgumentException("You must select either "
							+ "Write Through or Write Back: " + 
							hitWritingPolicy);
				}
			}
		}
		
		if (writeInMemory)
			mainMemory.storeWord(address, regData);
	}
	
	/**
	 * Stores all of the instructions that are in machine code in the memory
	 * @param assembler		The assembler that contains the instructions and
	 * 						the address of the first instruction
	 */
	public void storeInstructionsInMainMemory(Assembler assembler) {
		this.mainMemory.storeInstructions(assembler);
	}
	
	public String toString() {
		String L1Caches = "Instruction Cache\n" + this.instructionCacheL1.toString() + 
				"\n" + "Data Cache\n" + this.dataCacheL1.toString() + "\n";
		
		int cyclesSpentToAccessMemories = 0;
		
		String higherLevels = "";
		for (int i = 0; i < caches.length; i++) {
			higherLevels += "L" + (i + 2) + "\n" + caches[i].toString() + "\n";
			cyclesSpentToAccessMemories += caches[i].cyclesSpentToAccess();
		}
		
		String mainMemory = this.mainMemory.toString() + "\n";
		
		cyclesSpentToAccessMemories += this.mainMemory.cyclesSpentToAccess();
		String totalNumberOfCycles = "Cycles spent to access memories\n" + 
				cyclesSpentToAccessMemories + ",cycles";
		
		return L1Caches + higherLevels + mainMemory + totalNumberOfCycles;
		
	}
	
}
