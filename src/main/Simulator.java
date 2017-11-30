package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import assembler.Assembler;
import memoryHierarchy.MemoryHierarchy;
import misc.AdditionalMathFunctions;
import registerFile.RegisterFile;

public class Simulator {
	
	/**
	 * The memory hierarchy
	 */
	MemoryHierarchy memoryHierarchy;
	
	/**
	 * The register file
	 */
	RegisterFile registerFile;
	
	/**
	 * The number of instructions in the program that are given by the user
	 */
	int numberOfInstructions;
	
	/**
	 * The address of the first instruction of the program
	 */
	int addressOfFirstInstruction;
	
	/**
	 * The number of the executed instruction in the simulation
	 */
	int numberOfExecutedInstructions;
	
	
	/**
	 * initializes the assembler, caches, memory, register file,
	 * numberOfInstructions, addressOfFirstInstruction
	 * 
	 * @param SLM				A 2D array containing the cache geometry for every
	 * 							level
	 * 
	 * @param latencies			A 1D array containing the latency of every cache 
	 * 							level and the memory 
	 *
	 * @param instructions		The instructions written in assembly language
	 * @param startingAddress	The address of the first instruction
	 * @param data				The initial data that is to be loaded to the 
	 * 							memory
	 * 
	 * @param locations			The location of the initial data data
	 * @param hitWritingPolicy	The writing policy if hit
	 * @param missWritingPolicy The writing policy if missed
	 */
	Simulator(int[][] SLM, int[] latencies, String[] instructions, 
			String startingAddress, String[] data, String[] locations, 
			String hitWritingPolicy, String missWritingPolicy) {
		
		Assembler assembler = new Assembler(instructions, startingAddress);
		this.memoryHierarchy = new MemoryHierarchy(SLM, latencies, data, 
				locations, hitWritingPolicy, missWritingPolicy);
		
		this.memoryHierarchy.storeInstructionsInMainMemory(assembler);
		this.registerFile = new RegisterFile();
		
		this.numberOfInstructions = instructions.length;
		this.addressOfFirstInstruction = (short) Integer.parseInt(startingAddress, 16);
		this.numberOfExecutedInstructions = 0;
	}
	
	/**
	 * executes the instruction given its address in the main memory (executes 
	 * only load and store so far as they are the ones causing hits and misses)
	 * 
	 * @param PC	The address of the instruction that is to be executed
	 */
	void executeInstruction(short PC) {
		short instruction = AdditionalMathFunctions.byte2Short(memoryHierarchy.read(PC, true));
		
		if (Assembler.decodeInstruction(instruction, Assembler.OPCODE_STARTING_INDEX,
				Assembler.OPCODE_LENGTH) == Assembler.LW_OPCODE) {
			
			int regAIndex = Assembler.decodeInstruction(instruction, 
					Assembler.REG_A_STARTING_INDEX, 
					Assembler.REG_A_LENGTH);
			
			int regBIndex = Assembler.decodeInstruction(instruction, 
					Assembler.REG_B_STARTING_INDEX, 
					Assembler.REG_B_LENGTH);
			
			int immediate = Assembler.decodeInstruction(instruction, 
					Assembler.SIGNED_IMMDEDIATE7_STARTING_INDEX, 
					Assembler.SIGNED_IMMDEDIATE7_LENGTH);
			
			short address = (short) (registerFile.getRegisterData(regBIndex) + 
					AdditionalMathFunctions.adjustImmediate(immediate, 7));
			
			short data = AdditionalMathFunctions.byte2Short(memoryHierarchy.read(address, false));
			
			registerFile.putInRegister(regAIndex, data);
		}
		
		if (Assembler.decodeInstruction(instruction, Assembler.OPCODE_STARTING_INDEX,
				Assembler.OPCODE_LENGTH) == Assembler.SW_OPCODE) {
			
			int regAIndex = Assembler.decodeInstruction(instruction, 
					Assembler.REG_A_STARTING_INDEX, 
					Assembler.REG_A_LENGTH);
			
			int regBIndex = Assembler.decodeInstruction(instruction, 
					Assembler.REG_B_STARTING_INDEX, 
					Assembler.REG_B_LENGTH);
			
			int immediate = Assembler.decodeInstruction(instruction, 
					Assembler.SIGNED_IMMDEDIATE7_STARTING_INDEX, 
					Assembler.SIGNED_IMMDEDIATE7_LENGTH);
			
			short address = (short) (registerFile.getRegisterData(regBIndex) + 
					AdditionalMathFunctions.adjustImmediate(immediate, 7));
			
			short data = registerFile.getRegisterData(regAIndex);
			
			memoryHierarchy.write(address, data);
			
		}
		
		this.numberOfExecutedInstructions++;
	}
	
	/**
	 * Executes all the instructions
	 */
	public void executeAllInstructions() {
		for (int i = 0; i < this.numberOfInstructions; i++) {
			executeInstruction((short) (this.addressOfFirstInstruction + i * 2));
		}
	}
	
	public String toString() {
		String numberOfExecutedInstructions = "Number of executed instructions\n" + 
				this.numberOfExecutedInstructions + ",instructions";
		
		return this.registerFile.toString() + "\n" + this.memoryHierarchy.toString() + 
				"\n" + numberOfExecutedInstructions;
	}
	
	/**
	 * Writes this.toString() to a CSV file which is called "Data"
	 * @throws FileNotFoundException (occurs when the file is open)
	 */
	public void toCSV() throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File("Data.csv"));
		printWriter.write(this.toString());
		printWriter.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		/*
		 * L1 data and instruction caches:-
		 * 		S = 1KB,	L, 2B, 	M = 1
		 * 
		 * L2 cache:-
		 * 		S = 2KB,	L, 4B,	M = 2
		 */
		int[][] SLM = {{1024, 2, 1}, 
				{2048, 4, 2}};
		
		/*
		 * L1 caches latency = 2 Cycles
		 * L2 cache latency = 5 Cycles
		 * main memory latency = 30 Cycles
		 */
		int[] latencies = {2, 5, 30};
		
		// Program instructions
		String[] instructions = {"LW R1, R2, 0", 
				"SW R1, R4, 2", 
				"LW R7, R1, 0",
				"SW R7, R6, 4"};
		
		// The address of the first instruction
		String startingAddress = "1AAA";
		
		// The initial data that is to be loaded to the main memory
		String[] data = {"2", "2","A"};
		
		// The locations of the initial data
		String[] locations = {"0000", "FFF1", "FFF2"};
		
		// The writing policy if hit
		String hitWritingPolicy = "Write Through";
		
		// The writing policy if miss
		String missWritingPolicy = "Write Allocate";
		
		// Initializing the simulator
		Simulator simulator = new Simulator(SLM, latencies, instructions,
				startingAddress, data, locations, hitWritingPolicy, missWritingPolicy);
		
		// Executes all the instruction
		simulator.executeAllInstructions();
		
		// Writes the output to a CSV file
		simulator.toCSV();
	}

}
