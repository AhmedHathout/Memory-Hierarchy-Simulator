package registerTable;

import registerFile.RegisterFile;

public class RegisterTable {
	
	private int[] reorderBufferIndices;
	
	public RegisterTable() {
		this.reorderBufferIndices = new int[RegisterFile.NUMBER_OF_REGISTERS];
		initializeIndices();
	}

	private void initializeIndices() {
		for(int i = 0; i < reorderBufferIndices.length; i++)
			this.reorderBufferIndices[i] = -1;
	}
	
	public void associateRegisterWithROB(int registerIndex, int reorderBufferIndex) {
		this.reorderBufferIndices[registerIndex] = reorderBufferIndex;
	}
	
	public void removeRegister(int registerIndex) {
		this.reorderBufferIndices[registerIndex] = -1;
	}
	
	public int getROBIndex(int registerIndex) {
		return this.reorderBufferIndices[registerIndex];
	}

}
