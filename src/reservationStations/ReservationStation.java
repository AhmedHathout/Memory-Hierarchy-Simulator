package reservationStations;

import assembler.Assembler;

public class ReservationStation {
	
	/**
	 * The name of the reservation station and its number
	 */
	String name;
	
	/**
	 * Indicates whether this reservation station is busy or not
	 */
	boolean busy;
	
	/**
	 * The opcode of the instruction
	 */
	public int op = -1;
	
	/**
	 * Used to differentiate between RRR-type instructions
	 */
	private int function;

	/**
	 * The value of the first source register
	 */
	public short vj = -1;
	
	/**
	 * The value of the second source register
	 */
	public short vk = -1;
	
	/**
	 * 	The index of the ROB that will have the first source as soon as it is
	 * 	written
	 */
	int qj = -1;
	
	/**
	 * 	The index of the ROB that will have the second source as soon as it is
	 * 	written
	 */
	int qk = -1;
	
	/**
	 * The destination to the ROB
	 */
	public int dest;
	
	/**
	 * The address if the instruction is either load or store
	 */
	public short a;
	
	/**
	 * The latency of the functional unit
	 */
	int latency;
	
	/**
	 * The time (in cycles) left to execute this instruction
	 */
	int timeLeft;
	
	/**
	 * The first cycle when the instruction came to this reservation station
	 * and started execution
	 */
	int firstCycle;
	
	/**
	 * The address of the instruction that is in this reservation station
	 */
	short pc;
	
	/**
	 * Sets the values for that reservation station
	 * 
	 * @param op	The opcode of the instruction
	 * @param vj	The value of the first source register
	 * @param vk	The value of the second source register
	 * @param qj	The name of the reservation station that produces the first 
	 * 				source register
	 * 
	 * @param qk	The name of the reservation station that produces the second 
	 * 				source register
	 * @param dest 
	 * 
	 * @param a		The address if the instruction is either load or store
	 * 
	 * @param firstCycle	The first cycle when the instruction came to this 
	 * 						reservation station and started execution
	 */
	void insert(int op, int function, short vj, short vk, int qj, int qk, int dest, short a, int firstCycle, short pc) {
		this.busy = true;
		this.timeLeft = this.latency;
		this.op = op;
		this.function = function;
		this.vj = vj;
		this.vk = vk;
		this.qj = qj;
		this.qk = qk;
		this.dest = dest;
		this.a = a;
		this.firstCycle = firstCycle;
		this.pc = pc;
	}
	
	/**
	 * Removes a reservation station
	 * 
	 * @return	The address to be used if the instruction is load, store or branch
	 */
	short remove() {
		this.busy = false;
		// The next 2 lines are just in case
		this.qj = -1;
		this.qk = -1;
		return compute();
	}
	
	/**
	 * Decrements the timeLeft if it is not 0 and the in
	 * Used only by the execution stage
	 */
	public void decrementTimeLeft() {
		if (this.timeLeft != 0 && qj == -1 && qk == -1) {
			this.timeLeft--;
			
			if (op == Assembler.LW_OPCODE || op == Assembler.SW_OPCODE)
				calculateAddress();
		}
	}

	public short compute() {
		if (this.op == Assembler.LW_OPCODE || op == Assembler.SW_OPCODE)
			return this.a;
		
		switch(op) {
		
		case Assembler.ADD_OPCODE:
		case Assembler.ADDI_OPCODE:
			if (function == Assembler.ADD_FUNCTION) {
				return (short) (this.vj + this.vk);
			}
			
			else if (function == Assembler.NAND_FUNCTION) {
				return (short) (this.vj & (~ this.vk));
			}
			
		case Assembler.LW_OPCODE:
		case Assembler.SW_OPCODE:
			return this.a;
			
		// TODO
		case Assembler.BEQ_OPCODE:
		case Assembler.JALR_OPCODE:
			return 0;
		default:
			throw new IllegalArgumentException("This opcode does not have an "
					+ "FU: " + op);
			
		}
	}

	/**
	 * Calculates the address to use it in load, store or branch instructions
	 * and removes vj
	 */
	void calculateAddress() {
		this.a += this.vj;
		this.vj = 0;
	}
	
	public boolean finished() {
		return this.timeLeft == 0;
	}
	
}
