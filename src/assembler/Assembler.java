package assembler;

public class Assembler {
	
	/**
	 * Opcode			Function
	 * 	ADD	: 	000			0000
	 * 	SUB	:	000			0001
	 * 	MUL	:	000			0010
	 * 	NAND: 	000			0011
	 * 
	 * 	ADDI:	001			----
	 * 	JMP	:	010			----
	 * 	RET	:	011			----
	 * 	LW	:	100			----
	 * 	SW	: 	101			----
	 * 	BEQ	:	110			----
	 * 	JALR:	111			----
	 */
	
	/**
	 * The starting index of the opcode part in an instruction
	 */
	public static final int OPCODE_STARTING_INDEX = 13;
	
	/**
	 * The starting index of regA in an instruction
	 */
	public static final int REG_A_STARTING_INDEX = 10;
	
	/**
	 * The starting index of regB in an instruction
	 */
	public static final int REG_B_STARTING_INDEX = 7;
	
	/**
	 * The starting index of the function part in an instruction
	 */
	public static final int FUNCTION_STARTING_INDEX = 3;
	
	/**
	 * The starting index of regC in an instruction
	 */
	public static final int REG_C_STARTING_INDEX = 0;
	
	/**
	 * The starting index of the 7-bit signed immediate part in an instruction
	 */
	public static final int SIGNED_IMMDEDIATE7_STARTING_INDEX = 0;
	
	/**
	 * The starting index of the 10-bit signed immediate part in an instruction
	 */
	public static final int SIGNED_IMMDEDIATE10_STARTING_INDEX = 0;
	
	/**
	 * The number of bits used for the opcode
	 */
	public static final int OPCODE_LENGTH = 3;
	
	/**
	 * The number of bits used for regA
	 */
	public static final int REG_A_LENGTH = 3;
	
	/**
	 * The number of bits used for regB
	 */
	public static final int REG_B_LENGTH = 3;
	
	/**
	 * The number of bits used for the function
	 */
	public static final int FUNCTION_LENGTH = 4;
	
	/**
	 * The number of bits used for regC
	 */
	public static final int REG_C_LENGTH = 3;
	
	/**
	 * The number of bits used for the 7-bit signed immediate
	 */	
	public static final int SIGNED_IMMDEDIATE7_LENGTH = 7;
	
	/**
	 * The number of bits used for the 10-bit signed immediate
	 */	
	public static final int SIGNED_IMMDEDIATE10_LENGTH = 10;
	
	/**
	 * The opcode for the ADD instruction
	 */
	public static final int ADD_OPCODE = 0b000;
	
	/**
	 * The opcode for the SUB instruction
	 */
	public static final int SUB_OPCODE = 0b000;
	
	/**
	 * The opcode for the MUL instruction
	 */
	public static final int MUL_OPCODE = 0b000;
	
	/**
	 * The opcode for the NAND instruction
	 */
	public static final int NAND_OPCODE = 0b000;
	
	/**
	 * The opcode for the ADDI instruction
	 */
	public static final int ADDI_OPCODE = 0b001;
	
	/**
	 * The opcode for the JMP instruction
	 */
	public static final int JMP_OPCODE = 0b010;
	
	/**
	 * The opcode for the RET instruction
	 */
	public static final int RET_OPCODE = 0b011;
	
	/**
	 * The opcode for the LW instruction
	 */
	public static final int LW_OPCODE = 0b100;
	
	/**
	 * The opcode for the SW instruction
	 */
	public static final int SW_OPCODE = 0b101;
	
	/**
	 * The opcode for the BEQ instruction
	 */
	public static final int BEQ_OPCODE = 0b110;
	
	/**
	 * The opcode for the JALR instruction
	 */
	public static final int JALR_OPCODE = 0b111;
	
	/**
	 * The function for the add instruction
	 */
	public static final int ADD_FUNCTION = 0b0000;
	
	/**
	 * The function for the SUB instruction
	 */
	public static final int SUB_FUNCTION = 0b0001;
	
	/**
	 * The function for the MUL instruction
	 */
	public static final int MUL_FUNCTION= 0b0010;
	
	/**
	 * The function for the NAND instruction
	 */
	public static final int NAND_FUNCTION = 0b0011;
	
	/**
	 * The machine code that is resulted from assembling the instructions
	 */
	public short[] machineCode;
	
	/**
	 * The address of the first instruction
	 */
	public short startingAddress;
	
	
	/**
	 * Takes the instructions and the starting address and assembles these
	 * instructions
	 * 
	 * @param	stringInstructions The instructions written in plain assembly language
	 * @param	startingAddress The starting address of the instructions in the 
	 * main memory
	 */
	public Assembler(String[] stringInstructions, String startingAddress) {
		this.machineCode = new short[stringInstructions.length];
		Instruction[] instructions = initializeInstructions(stringInstructions);
		
		this.startingAddress = (short) Integer.parseInt(startingAddress, 16);
		
		assemble(instructions);
	}
	
	/**
	 * Takes the instructions written in plain assembly language and returns 
	 * a 1D array of type instruction
	 * 
	 * @param	stringInstructions The instructions written in plain assembly 
	 * language
	 * 
	 * @return	a 1D array of type instruction
	 */
	Instruction[] initializeInstructions(String[] stringInstructions) {
		Instruction[] instructions = new Instruction[stringInstructions.length];
		
		for (int i = 0; i < instructions.length; i++) 
			instructions[i] = new Instruction(stringInstructions[i]);
		
		return instructions;
	}
	
	/**
	 * Takes a 1D array of type instruction and computes the machine code for
	 * these instructions
	 * 
	 * @param instructions a 1D array that contains the instructions of type
	 * instruction 
	 */
	private void assemble(Instruction[] instructions) {
		
		for (int i = 0; i < instructions.length; i++) {
			Instruction currentInstruction = instructions[i];
			
			if (currentInstruction.opcode.equalsIgnoreCase("ADD")) {
				machineCode[i] = (short) ((ADD_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(ADD_FUNCTION << FUNCTION_STARTING_INDEX) + 
						(currentInstruction.regC << REG_C_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("SUB")) {
				machineCode[i] = (short) ((SUB_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(SUB_FUNCTION << FUNCTION_STARTING_INDEX) + 
						(currentInstruction.regC << REG_C_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("MUL")) {
				machineCode[i] = (short) ((MUL_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(MUL_FUNCTION << FUNCTION_STARTING_INDEX) + 
						(currentInstruction.regC << REG_C_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("NAND")) {
				machineCode[i] = (short) ((NAND_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(NAND_FUNCTION << FUNCTION_STARTING_INDEX) + 
						(currentInstruction.regC << REG_C_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("ADDI")) {
				machineCode[i] = (short) ((ADDI_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(currentInstruction.signedImmediate7 << SIGNED_IMMDEDIATE7_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("JMP")) {
				machineCode[i] = (short) ((JMP_OPCODE << OPCODE_STARTING_INDEX) +
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.signedImmediate10 << SIGNED_IMMDEDIATE10_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("RET")) {
				machineCode[i] = (short) ((RET_OPCODE << OPCODE_STARTING_INDEX) +
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.signedImmediate10 << SIGNED_IMMDEDIATE10_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("LW")) {
				machineCode[i] = (short) ((LW_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(currentInstruction.signedImmediate7 << SIGNED_IMMDEDIATE7_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("SW")) {
				machineCode[i] = (short) ((SW_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(currentInstruction.signedImmediate7 << SIGNED_IMMDEDIATE7_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("BEQ")) {
				machineCode[i] = (short) ((BEQ_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX) + 
						(currentInstruction.signedImmediate7 << SIGNED_IMMDEDIATE7_STARTING_INDEX));
			}
			
			else if (currentInstruction.opcode.equalsIgnoreCase("JALR")) {
				machineCode[i] = (short) ((JALR_OPCODE << OPCODE_STARTING_INDEX) + 
						(currentInstruction.regA << REG_A_STARTING_INDEX) + 
						(currentInstruction.regB << REG_B_STARTING_INDEX));
			}
		}
	}
	
	/**
	 * Given the instruction in machine code, the starting index of the part of
	 * the instruction that is required (opcode for example) and the length of 
	 * it, this method returns that part
	 * 
	 * @param instruction	The instruction in machine code
	 * @param startingIndex	The starting index of the required part
	 * @param length		The length of that part
	 * 
	 * @return				The required part of that instruction
	 */
	public static short decodeInstruction(short instruction, int startingIndex, int length) {
		int ones = (int) (Math.pow(2, length) - 1);
		
		return (short) ((instruction >> startingIndex) & ones);
	}
	
	public static void main(String[] args) {
		String[] instructions = {"ADD R1, R2, R3", 
				"SUB R4, R5, R6", 
				"MUL R7, R0, R1", 
				"NAND R2, R3, R4", 
				"ADDI R5, R6, 3", 
				"JMP R7, 511",
				"RET R0", 
				"LW R1, R2, 0", 
				"SW R3, R4, 1", 
				"BEQ R5, R6, 4", 
				"JALR R7, R0"};
		
		System.out.println(1 << 5);
		
		Assembler assembler = new Assembler(instructions, "555");
		for (int i = 0; i < assembler.machineCode.length; i++) 
			System.out.println(assembler.machineCode[i]);
		
	}
	
}
