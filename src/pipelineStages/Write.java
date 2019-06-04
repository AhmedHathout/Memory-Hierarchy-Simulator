package pipelineStages;

import assembler.Assembler;
import memoryHierarchy.MemoryHierarchy;
import misc.AdditionalMathFunctions;
import pc.PC;
import registerFile.RegisterFile;
import reservationStations.ReservationStation;
import reservationStations.ReservationStations;

public class Write extends Stage{
	
	ReservationStations reservationStations;
	
	RegisterFile registerFile;
	
	MemoryHierarchy memoryHierarchy;
	
	PC pc;
	
	boolean finished;
	
	Write (ReservationStations reservationStations, RegisterFile registerFile, 
			MemoryHierarchy memoryHierarchy, PC pc) {
		
		this.reservationStations = reservationStations;
		this.registerFile = registerFile;
		this.memoryHierarchy = memoryHierarchy;
		this.pc = pc;
	}
	
	@Override
	public void run() {
		
		//TODO delete station
		ReservationStation toRegister = reservationStations.getFirstToRegister();
		ReservationStation toMemory = reservationStations.getFirstToMemory();
		
		if (toRegister != null) {
			short data = toRegister.compute();
			int registerIndex = toRegister.dest;
			
			if (registerIndex == 8)
				if (toRegister.op == Assembler.BEQ_OPCODE) {
					if (toRegister.vj == toRegister.vk)
						this.pc.setPC((short) (pc.getPC() + toRegister.a));
				}
			
				else if (toRegister.op == Assembler.JALR_OPCODE) {
					short newPC = toRegister.vj;
					registerFile.putInRegister(registerIndex, (short) 
							(pc.getPC() + 2));
					pc.setPC(newPC);
				}
				
				else
					throw new IllegalArgumentException("Why the f**k does "
							+ "the register index equal 8");
					
			else if (registerIndex != -1)
				registerFile.putInRegister(registerIndex, data);
			System.out.println(memoryHierarchy.toString());
		}
		
		// TODO handle memory
		if (toMemory != null) {
			if (toMemory.op == Assembler.LW_OPCODE) {
				short address = toMemory.a;
				short data = AdditionalMathFunctions.byte2Short(
						memoryHierarchy.read(address, false));
				
				int registerIndex = toMemory.dest;
				registerFile.putInRegister(registerIndex, data);
			}
			
			else if (toMemory.op == Assembler.SW_OPCODE) {
				short address = toMemory.a;
				short data = toMemory.vk;
				memoryHierarchy.write(address, data);
			}
			
			else
				throw new IllegalArgumentException("Why the f**k does this opcode want to "
						+ "access the memory: " + toMemory.op);
			System.out.println(memoryHierarchy.toString());
		}
		this.pc.setPC((short) (this.pc.getPC() + 2));
		this.finished = reservationStations.areAllEmpty();
	}
	
}
