package registerFile;

public class RegisterFile {
	
	/**
	 * The number of registers in the register file
	 */
	public static final int NUMBER_OF_REGISTERS = 8;
	
	/**
	 * An array of type short
	 */
	short registers[];
	
	/**
	 * Initializes the register file with 8 registers
	 */
	public RegisterFile() {
		this.registers = new short[NUMBER_OF_REGISTERS];
	}
	
	/**
	 * Reads the data from a register indexed by registerIndex
	 * 
	 * @param registerIndex	Register index
	 * @return				The data contained in that register
	 */
	public short getRegisterData(int registerIndex) {
		return this.registers[registerIndex];
	}
	
	/**
	 * Stores the given data data in the register indexed by registerIndex
	 * 
	 * @param registerIndex		Register index
	 * @param data				The data that is to be stored in the register
	 */
	public void putInRegister(int registerIndex, short data) {
		if (registerIndex != 0)
			this.registers[registerIndex] = data;
	}
	
	public String toString() {
		String name = "Register File\n";
		String header = "Register Index,Register Content\n";
		
		String data = "";
		for (int i = 0; i < this.registers.length; i++) {
			data += i + "," + registers[i] + "\n";
		}
		
		return name + header + data;
	}
}
