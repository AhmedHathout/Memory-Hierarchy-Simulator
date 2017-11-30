package memoryHierarchy.mainMemory;

import java.util.Arrays;

import assembler.Assembler;

public class MainMemory {
	
	/**
	 * The size of the memory in bytes
	 */
	private static final int MEMORY_SIZE = (int) Math.pow(2, 16);
	
	/**
	 * Memory data
	 */
	public byte[] data;
	
	/**
	 * The access time of the memory
	 */
	private int latency;
	
	/**
	 * How many times the memory was accessed
	 */
	public int numOfAccesses;
	
	/**
	 * Constructs a memory with no initial data
	 * @param latency	The access time of the memory
	 */
	public MainMemory(int latency) {
		this.latency = latency;
		this.data = new byte[MEMORY_SIZE];
		this.numOfAccesses = 0;
	}
	
	/**
	 * Constructs a memory with initial data
	 * 
	 * @param latency		The latency of the memory
	 * @param initialData	The initial data of the memory
	 * @param locations		The location of these initial data
	 */
	public MainMemory(int latency, String[] initialData, String[] locations) {
		this(latency);
		
		for (int i = 0; i < initialData.length; i++) {
			int address = Integer.parseInt(locations[i], 16);
			this.data[address] = (byte) Integer.parseInt(initialData[i], 16);
		}
	}
	
	/**
	 * Loads the line that contains the word that is addressed by address
	 * 
	 * @param address					The address of the required word
	 * @param l							The size of the line of the highest cache
	 * 									Level
	 * 
	 * @param highestCacheDisplacement	The displacement of the required word in
	 * 									The highest cache
	 * 
	 * @return							The required word and the data around it
	 * 									to fit in the line
	 */
	public byte[] load(short address, short l, short highestCacheDisplacement) {
		this.numOfAccesses++;
		return Arrays.copyOfRange(this.data, address - highestCacheDisplacement, address + l);
	}
	
	/**
	 * Stores a line data from the highest level cache 
	 * 
	 * @param address					The address of the word that is to be 
	 * 									stored
	 * 
	 * @param data						The data of from the highest cache level
	 * 
	 * @param highestCacheDisplacement	The displacement of the word that is 
	 * 									addressed by address
	 */
	public void storeLine(short address, byte[] data, short highestCacheDisplacement) {
		this.numOfAccesses++;
		System.arraycopy(data, 0, this.data, address - highestCacheDisplacement, data.length);
	}
	
	/**
	 * Stores a word given its address
	 * @param address	The address where the data is to be stored
	 * @param data		The data came from a register
	 */
	public void storeWord(short address, short data) {
		this.numOfAccesses++;
		
		byte rightByte = (byte) data;
		byte leftByte = (byte) (data >> 8);
		this.data[address] = rightByte;
		this.data[address + 1] = leftByte;
	}
	
	/**
	 * Stores all of the instructions that are in machine code in the memory
	 * @param assembler		The assembler that contains the instructions and
	 * 						the address of the first instruction
	 */
	public void storeInstructions(Assembler assembler) {
		short baseAddress = assembler.startingAddress;
		short[] assembly = assembler.machineCode;
		
		for (short offset = 0; offset < assembly.length; offset++) {
			storeWord((short) (baseAddress + offset * 2), assembly[offset]);
			this.numOfAccesses--;
		}
	}
	
	/**
	 * @return	Total number of cycles spent to access the memory
	 */
	public int cyclesSpentToAccess() {
		return latency * numOfAccesses;
	}
	
	public String toString() {
		String name = "Main Memory\n";
		String header = "adress,data\n";
		
		String data = "";
		for (int i = 0; i < MEMORY_SIZE; i++) {
			if (this.data[i] != 0)
				data += Integer.toHexString(i) + "," + this.data[i] + "\n";
		}
		
		String numOfAccesses = "Number of accesses\n" + this.numOfAccesses + "\n";
		return name + header + data + numOfAccesses;		
	}
}
