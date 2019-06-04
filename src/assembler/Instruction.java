package assembler;

import java.util.StringTokenizer;

public class Instruction {
	
	/**
	 * The string opcode
	 */
	String opcode;
	
	/**
	 * The index of regA
	 */
	int regA;
	
	/**
	 * The index of regB
	 */
	int regB;
	
	/**
	 * The index of regC
	 */
	int regC;
	
	/**
	 * The 7-bit signed immediate
	 */
	int signedImmediate7;
	
	/**
	 * The 10-bit signed immediate
	 */
	int signedImmediate10;
	
	/**
	 * Takes an instruction written in plain assembly language to initialize 
	 * Instruction 
	 * 
	 * @param	instruction An instruction written in plain assembly language
	 * 
	 * @throws	IllegalArgumentException if the opcode is not defined or the
	 * 			index of the register is greater than 7
	 */
	Instruction(String instruction) {
		String[] preparedInstruction = prepareInstruction(instruction);

		this.opcode = preparedInstruction[0];
		this.regA = Integer.parseInt(preparedInstruction[1]);
		
		if (regA > 7) {
			throw new IllegalArgumentException("No such Register: " + regA);
		}
		
		if (opcode.equalsIgnoreCase("JMP"))
			this.signedImmediate10 = Integer.parseInt(preparedInstruction[2]);
		
		else if (!opcode.equalsIgnoreCase("RET")) {
			this.regB = Integer.parseInt(preparedInstruction[2]);
			
			if (regB > 7) {
				throw new IllegalArgumentException("No such Register: " + regB);
			}
			
			if (!opcode.equalsIgnoreCase("JALR"))
				if (opcode.equalsIgnoreCase("LW") || 
						opcode.equalsIgnoreCase("SW") ||
						opcode.equalsIgnoreCase("BEQ") || 
						opcode.equalsIgnoreCase("ADDI"))
					this.signedImmediate7 = Integer.parseInt(preparedInstruction[3]);
				
				else if (opcode.equalsIgnoreCase("ADD") || 
						opcode.equalsIgnoreCase("SUB") ||
						opcode.equalsIgnoreCase("MUL") || 
						opcode.equalsIgnoreCase("NAND")) {
					
					this.regC = Integer.parseInt(preparedInstruction[3]);
					if (regC > 7) {
						throw new IllegalArgumentException("No such Register: " + regC);
					}
				}
				else
					throw new IllegalArgumentException("No such opcode: " + opcode);
		}
	}
	
	/**
	 * Prepares the instruction by splitting it by ' ' and ',' and removing R's
	 * before the register number
	 * 
	 * @param instruction	The instruction written in plain assembly language
	 * @return				A 1D array that contains the prepared instruction
	 */
	private static String[] prepareInstruction(String instruction) {
		return getRegisterNumber(splitInstruction(instruction));
	}

	/**
	 * Takes the String instruction and splits it by ' ' or ',' to an array of 
	 * strings
	 * 
	 * @param instruction	The string array
	 * @return				The split String
	 */
	private static String[] splitInstruction(String instruction) {
		
		StringTokenizer tokenizedInstruction = new StringTokenizer(instruction, " ,");
		String[] splitInstruction = new String[tokenizedInstruction.countTokens()];
		
		for (int i = 0; i < splitInstruction.length; i++) 
			splitInstruction[i] = tokenizedInstruction.nextToken();
		
		return splitInstruction;
		
	}
	
	/**
	 * Takes an array that contains the split instruction and removes all the
	 * R's or r's in it and leaves the number of the register only
	 * 
	 * @param splitInstruction the instruction split by ' ' and ','
	 * 
	 * @return	the split instruction without the letter R before the register 
	 * 			number
	 */
	private static String[] getRegisterNumber(String[] splitInstruction) {
		String[] formattedInstruction = new String[splitInstruction.length];
		
		formattedInstruction[0] = splitInstruction[0];
		for (int i = 1; i < formattedInstruction.length; i++)
				formattedInstruction[i] = splitInstruction[i].replace("R", "").replace("r", "");
		return formattedInstruction;
	}
	
	public String toString() {
		return this.opcode + " " + this.regA + " " + this.regB + " " + this.regC + " " + this.signedImmediate7 + " " + this.signedImmediate10;
	}
	
}
