package assembler;

public class Program {
	
	Instruction[] instructions;
	
	private short startingAddress;
	
	Program(String[] instructions, short startingAddress) {
		this.instructions = new Instruction[instructions.length];
		this.startingAddress = startingAddress;
		
		initializeInstructions(instructions);
	}

	void initializeInstructions(String[] instructions) {
		for (int i = 0; i < this.instructions.length; i++) 
			this.instructions[i] = new Instruction(instructions[i]);
	}
	
}
