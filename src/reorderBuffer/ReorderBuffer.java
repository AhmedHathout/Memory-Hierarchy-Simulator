package reorderBuffer;

import assembler.Assembler;
import reservationStations.ReservationStations;

public class ReorderBuffer {
	
	/**
	 * The entries of the reorder buffer
	 */
	ReorderBufferQueue entries;
	
	/**
	 * Initializing an empty reorder buffer
	 * 
	 * @param numberOfEntries	The size of the buffer
	 */
	public ReorderBuffer(int numberOfEntries) {
		entries = new ReorderBufferQueue(numberOfEntries);
	}
	
	// TODO comments
	/**
	 * Enqueues the instruction in the ROB
	 * 
	 * @param instructionToIssue	The instruction in machine code
	 * 
	 * @return						True if inserted, false otherwise
	 */
	public int enqueueEntry(int type, int destination) {
		ReorderBufferEntry entry = new ReorderBufferEntry(type, destination);
		return entries.enqueue(entry);
	}
	
	/**
	 * Removes the first entry in the ROB
	 * 
	 * @return	The entry or false if it is not committed or the queue is empty
	 */
	public ReorderBufferEntry dequeue() {
		return entries.dequeue();
	}
	
	/**
	 * Sets the value of the entry indexed to be value
	 * 
	 * @param index	The index of the entry in the ROB
	 * @param value	The value that is to be in that entry
	 */
	public void setValue(int index, short value) {
		entries.setValue(index, value);
	}
	
	public ReorderBufferEntry getEntry(int index) {
		return entries.getEntry(index);
	}
	
	public boolean isFull() {
		return this.entries.isFull();
	}
	
	public boolean isEmpty() {
		return this.entries.isEmpty();
	}	
}
