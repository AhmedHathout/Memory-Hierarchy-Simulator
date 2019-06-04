package reorderBuffer;

public class ReorderBufferQueue {
	
	/**
	 * The entries of the queue
	 */
	ReorderBufferEntry[] entries;
	
	/**
	 * The Head of the queue
	 */
	int head;
	
	/**
	 * The tail of the queue
	 */
	int tail;
	
	/**
	 * The number of entries in the queue
	 */
	int currentSize;
	
	/**
	 * Initialize the queue to be of size numberOfEntries
	 * @param numberOfEntries	The size of the queue
	 */
	ReorderBufferQueue(int numberOfEntries) {
		this.entries = new ReorderBufferEntry[numberOfEntries];
	}
	
	int enqueue(ReorderBufferEntry entry) {
		if (isFull())
			return -1;
		
		entries[tail] = entry;
		currentSize++;
		return tail++;
	}
	
	ReorderBufferEntry dequeue() {
		if (isEmpty()|| !entries[head].ready)
			return null;
		currentSize--;
		return entries[head++];
	}
	
	void setValue(int index, short value) {
		entries[index].ready = true;
		entries[index].value = value;
	}
	
	ReorderBufferEntry getEntry(int index) {
		return entries[index];
	}
	
	boolean isFull() {
		if (currentSize == entries.length)
			return true;
		return false;
	}
	
	boolean isEmpty() {
		if (currentSize == 0)
			return true;
		return false;
	}
}
