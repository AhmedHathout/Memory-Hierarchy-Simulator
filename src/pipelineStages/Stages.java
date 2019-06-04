package pipelineStages;

import memoryHierarchy.MemoryHierarchy;
import pc.PC;
import registerFile.RegisterFile;
import registerTable.RegisterTable;
import reorderBuffer.ReorderBuffer;
import reservationStations.ReservationStations;

public class Stages{
	
	Fetch fetch;
	
	Issue issue;
	
	Execute execute;
	
	Write write;
	
	public Stages (MemoryHierarchy memoryHierarchy, String firstInstructionAddress,
			short numberOfInstructions, RegisterFile registerFile, 
			int[] numberOfFunctionalUnits, int numberOfROBEntries) {
		
		ReservationStations reservationStations = new ReservationStations(numberOfFunctionalUnits);
		ReorderBuffer reorderBuffer = new ReorderBuffer(numberOfROBEntries);
		RegisterTable registerTable = new RegisterTable();
		PC pc = new PC((short) (Integer.parseInt(firstInstructionAddress, 16) - 2));
		
		this.fetch = new Fetch(memoryHierarchy, 
				(short) Integer.parseInt(firstInstructionAddress, 16), 
				numberOfInstructions, pc);
		
		this.issue = new Issue(reservationStations, reorderBuffer, registerFile, 
				registerTable, pc);
		this.execute = new Execute(reservationStations);
		this.write = new Write(reservationStations, registerFile, 
				memoryHierarchy, pc);
		
	}

	public boolean run(int currentCycle) {
		
		write.run();
		execute.run();
		short instruction = fetch.getInstruction();
		boolean isValid = fetch.isValid();
		issue.setAttributes(instruction, isValid, currentCycle);
		issue.run();
		
		boolean wasIssued = issue.wasIssued();
		fetch.issued(wasIssued);
		fetch.run();
		
		return finished();
		
	}

	public boolean finished() {
		return fetch.finished && !issue.validInstruction && write.finished;
	}
	
	
	
}
