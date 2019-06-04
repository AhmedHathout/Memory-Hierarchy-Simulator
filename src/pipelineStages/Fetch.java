package pipelineStages;

import memoryHierarchy.MemoryHierarchy;
import misc.AdditionalMathFunctions;
import pc.PC;

public class Fetch extends Stage{
	
	MemoryHierarchy memoryHierarchy;
	
	PC pc;
	
	short firstInstructionAddress;
	
	short lastInstructionAddress;
	
	short currentInstruction;
	
	boolean lastIssued;
	
	boolean finished;
	
	Fetch(MemoryHierarchy memoryHierarchy, short firstInstructionAddress, 
			short numberOfInstructions, PC pc) {
		
		this.memoryHierarchy = memoryHierarchy;
		this.firstInstructionAddress = firstInstructionAddress;
		this.lastInstructionAddress = (short) (firstInstructionAddress + 2 * 
				numberOfInstructions);
		
		this.pc = pc;
		pc.setPC((short) (firstInstructionAddress - 2));
		this.lastIssued = true;
	}
	
	@Override
	public void run() {
		if (this.lastIssued && pc.getPC() <= lastInstructionAddress) {
			this.currentInstruction = AdditionalMathFunctions.byte2Short(
					memoryHierarchy.read(pc.getPC(), true));
			this.lastIssued = false;
		}
		
		else
			this.finished = true;
	}
	
	void issued(boolean wasIssued) {
		this.lastIssued = wasIssued;
	}
	
	short getInstruction() {
		return this.currentInstruction;
	}
	
	boolean isValid() {
		return !this.lastIssued;
	}
	
	void setPC(short pc) {
		this.pc.setPC(pc);
	}
	
}
