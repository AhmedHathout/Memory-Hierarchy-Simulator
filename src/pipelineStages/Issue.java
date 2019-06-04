package pipelineStages;

import java.nio.channels.AsynchronousServerSocketChannel;

import assembler.Assembler;
import misc.AdditionalMathFunctions;
import pc.PC;
import registerFile.RegisterFile;
import registerTable.RegisterTable;
import reorderBuffer.ReorderBuffer;
import reorderBuffer.ReorderBufferEntry;
import reservationStations.ReservationStations;

public class Issue extends Stage{
	
	ReservationStations reservationStations;
	
	ReorderBuffer reorderBuffer;
	
	RegisterTable registerTable;
	
	RegisterFile registerFile;
	
	PC pc;
	
	short instructionToIssue;
	
	int currentCycle;
	
	boolean validInstruction;
	
	boolean issued;
	
	Issue(ReservationStations reservationStations, ReorderBuffer reorderBuffer, 
			RegisterFile registerFile, RegisterTable registerTable, PC pc) {
		
		this.reservationStations = reservationStations;
		this.reorderBuffer = reorderBuffer;
		this.registerFile = registerFile;
		this.registerTable = registerTable;
		this.pc = pc;
		
	}
	
	@Override
	public void run() {
		if (this.validInstruction) {
			
//			if (reorderBuffer.isFull()) {
//				this.issued = false;
//				return;
//			}
			
			int op = Assembler.decodeInstruction(this.instructionToIssue, 
					Assembler.OPCODE_STARTING_INDEX, Assembler.OPCODE_LENGTH);
			
			int destinationRegister;
			int type;
			
			switch(op) {
			
			case Assembler.ADD_OPCODE: // This goes for ADD and NAND
			case Assembler.ADDI_OPCODE:
				type = ReservationStations.ALU;break;
				
			case Assembler.LW_OPCODE:
				type = ReservationStations.LOAD;break;
				
			case Assembler.SW_OPCODE:
				type = ReservationStations.STORE;break;
				
			case Assembler.BEQ_OPCODE:
			case Assembler.JALR_OPCODE:
				type = ReservationStations.BRANCH;break;
			default:
				throw new IllegalArgumentException("This opcode does not have an "
						+ "FU: " + op);
				
			}
			
			int reservationStationIndex = reservationStations.isFree(type);
			if (reservationStationIndex == -1) {
				this.issued = false;
				return;
			}
			
			short vj;
			int qj; 
			
			int registerBIndex = Assembler.decodeInstruction(this.instructionToIssue, 
					Assembler.REG_B_STARTING_INDEX, Assembler.REG_B_LENGTH);
			
			int[] VandQ = getVandQ(registerBIndex);
			vj = (short) VandQ[0];
			qj = VandQ[1];
			
			short vk;
			int qk;
			short a = 0;
			int reorderBufferDestination;
			
			if (op == Assembler.ADD_OPCODE) {// This goes for ADD and NAND
				destinationRegister = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.REG_A_STARTING_INDEX, Assembler.REG_A_LENGTH);
				
				int registerCIndex = Assembler.decodeInstruction(
						this.instructionToIssue, Assembler.REG_C_STARTING_INDEX, 
						Assembler.REG_C_LENGTH);
				
				VandQ = getVandQ(registerCIndex);
				vk = (short) VandQ[0];
				qk = VandQ[1];
				
				reorderBufferDestination = Assembler.decodeInstruction(
						this.instructionToIssue, Assembler.REG_A_STARTING_INDEX, 
						Assembler.REG_A_LENGTH);
			}
			
			// TODO calculate ROB destination
			else if (op == Assembler.BEQ_OPCODE) {
				int registerAIndex = Assembler.decodeInstruction(
						this.instructionToIssue, Assembler.REG_A_STARTING_INDEX, 
						Assembler.REG_A_LENGTH);
				
				destinationRegister = -1;
				
				VandQ = getVandQ(registerAIndex);
				vk = (short) VandQ[0];
				qk = VandQ[1];
				reorderBufferDestination = 8;
				
				a = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.SIGNED_IMMDEDIATE7_STARTING_INDEX, 
						Assembler.SIGNED_IMMDEDIATE7_LENGTH);
				
				a = (short) AdditionalMathFunctions.adjustImmediate(a, 7);
			}
			
			else if (op == Assembler.ADDI_OPCODE) {
				destinationRegister = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.REG_A_STARTING_INDEX, Assembler.REG_A_LENGTH);
				qk = -1;
				int immediate = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.SIGNED_IMMDEDIATE7_STARTING_INDEX, 
						Assembler.SIGNED_IMMDEDIATE7_LENGTH);
				
				vk = (short) AdditionalMathFunctions.adjustImmediate(immediate, 7);
				
				reorderBufferDestination = Assembler.decodeInstruction(
						this.instructionToIssue, Assembler.REG_A_STARTING_INDEX, 
						Assembler.REG_A_LENGTH);
			}
			
			//TODO memory address is -1
			else if (op == Assembler.LW_OPCODE || op == Assembler.SW_OPCODE) {
				vk = 0;
				qk = -1;
				a = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.SIGNED_IMMDEDIATE7_STARTING_INDEX, 
						Assembler.SIGNED_IMMDEDIATE7_LENGTH);
				
				a = (short) AdditionalMathFunctions.adjustImmediate(a, 7);
				
				if (op == Assembler.LW_OPCODE) {
					
					reorderBufferDestination = Assembler.decodeInstruction(
							this.instructionToIssue, Assembler.REG_A_STARTING_INDEX, 
							Assembler.REG_A_LENGTH);
					
					destinationRegister = Assembler.decodeInstruction(this.instructionToIssue, 
							Assembler.REG_A_STARTING_INDEX, Assembler.REG_A_LENGTH);
				}
				else {
					reorderBufferDestination = -1;
					destinationRegister = -1;
				}
			}
			
			// TODO still need to handle JALR
			else if (op == Assembler.JALR_OPCODE) {
				reorderBufferDestination = -1;
				vk = 0;
				qk = 0;
				
				destinationRegister = Assembler.decodeInstruction(this.instructionToIssue, 
						Assembler.REG_A_STARTING_INDEX, Assembler.REG_A_LENGTH);
			}
			
			else
				throw new IllegalArgumentException("This opcode does not have an "
						+ "FU: " + op);
//			int reorderBufferIndex = reorderBuffer.enqueueEntry(type, reorderBufferDestination);
			int function = Assembler.decodeInstruction(this.instructionToIssue
					, Assembler.FUNCTION_STARTING_INDEX, 
					Assembler.FUNCTION_LENGTH);
			
			reservationStations.putInReservationStation(op, function, vj, vk, 
					qj, qk, destinationRegister, a, currentCycle, pc.getPC());
			
			this.issued = true;
			this.validInstruction = false;
		}
		this.issued = true;
	}
	
	private int[] getVandQ(int registerIndex) {
		int[] VandQ = new int[2];
		int ROBIndex = registerTable.getROBIndex(registerIndex);
		if (ROBIndex == -1) {
			VandQ[0] = registerFile.getRegisterData(registerIndex);
			VandQ[1] = -1;
		}
		
		else {
			ReorderBufferEntry entry = reorderBuffer.getEntry(ROBIndex);
			if (entry.isReady()) {
				VandQ[0] = entry.getValue();
				VandQ[1] = -1;
			}
			
			else {
				VandQ[1] = ROBIndex;
				VandQ[0] = 0;
			}
		}
		
		return VandQ;
	}
	
	public void setInstruction(short instruction) {
		this.instructionToIssue = instruction;
	}
	
	public void validateInstruction(boolean isValid) {
		validInstruction = isValid;
	}
	
	public void setAttributes(short instruction, boolean isValid, 
			int currentCycle) {
		
		this.instructionToIssue = instruction;
		this.validInstruction = isValid;
		this.currentCycle = currentCycle;
	}
	
	boolean wasIssued() {
		return this.issued;
	}
	
}
