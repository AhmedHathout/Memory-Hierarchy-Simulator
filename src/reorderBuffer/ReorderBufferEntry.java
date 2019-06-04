package reorderBuffer;

public class ReorderBufferEntry {
	
	/**
	 * The type of the instruction
	 */
	int type;
	
	/**
	 * The destination register after the instruction is committed
	 */
	int destination;
	
	/**
	 * The result of executing the instruction
	 */
	short value;
	
	/**
	 * Indicates whether the instruction has finished writing
	 */
	boolean ready;
	
	ReorderBufferEntry(int type, int destination) {
		this.type = type;
		this.destination = destination;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public short getValue() {
		return this.value;
	}
	
}
