package reservationStations;

import assembler.Assembler;

public class ReservationStations {
	
	/**
	 * The index of the ALU RSs in the stations array
	 */
	public static final int ALU = 0;
	
	/**
	 * The index of the ALU RSs in the stations array
	 */
	public static final int LOAD = 1;
	
	/**
	 * The index of the ALU RSs in the stations array
	 */
	public static final int STORE = 2;
	
	/**
	 * The index of the ALU RSs in the stations array
	 */
	public static final int BRANCH = 3;
	
	/**
	 * The array of available RS
	 */
	ReservationStation[][] stations;
	
	/**
	 * Initializes the reservation stations
	 * 
	 * @param numberOfReservaionStations	number of reservation stations
	 */
	public ReservationStations(int[] numberOfFunctionalUnits) {
		stations = new ReservationStation[4][];
		
		initializeStations(numberOfFunctionalUnits);
	}
	
	/**
	 * Initializes the reservation stations
	 * @param numberOfBranches 
	 * @param numberOfStores 
	 * @param numberOfLoads 
	 * @param numberOfALUs 
	 */
	private void initializeStations(int[] numberOfFunctionalUnits) {
		for (int i = 0; i < stations.length; i++) {
			stations[i] = new ReservationStation[numberOfFunctionalUnits[i]];
			for(int j = 0; j < stations[i].length; j++) 
				stations[i][j] = new ReservationStation();
		}
	}
	
	// TODO PC
	public boolean putInReservationStation(int op, int function, short vj, short vk
			, int qj, int qk, int dest, short a, int firstCycle, short pc) {
		int type;
		
		switch(op) {
		
			case Assembler.ADD_OPCODE: // This goes for ADD and NAND
			case Assembler.ADDI_OPCODE:
				type = ALU;break;
				
			case Assembler.LW_OPCODE:
				type = LOAD;break;
				
			case Assembler.SW_OPCODE:
				type = STORE;break;
				
			case Assembler.BEQ_OPCODE:
			case Assembler.JALR_OPCODE:
				type = BRANCH;break;
			default:
				throw new IllegalArgumentException("This opcode does not have an "
						+ "FU: " + op);
				
		}
				
		int index = isFree(type);
		if (index == -1) 
			return false;
		
		this.stations[type][index].insert(op, function, vj, vk, qj, qk, dest, a, firstCycle, pc);
		return true;
	}
	
	public int isFree(int type) {
		for (int i = 0; i < stations[type].length; i++)
			if (!stations[type][i].busy)
				return i;
		
		return -1;
	}
	
	public void decrementTime() {
		for (int i = 0; i < stations.length; i++) {
			for (int j = 0; j < stations[i].length; j++) {
				stations[i][j].decrementTimeLeft();
			}
		}
	}
	
	public ReservationStation getFirstToRegister() {
		for (int i = 0; i < stations.length; i++) {
			if (i == LOAD || i == STORE)	// load is to memory not register
				i++;
			for (int j = 0; j < stations[i].length; j++) {
				ReservationStation currentRS = stations[i][j];
				
				if (currentRS.finished() && currentRS.busy) {
					currentRS.busy = false;
					return currentRS;
				}
			}
		}
		
		return null;
	}
	
	public ReservationStation getFirstToMemory() {
		
		for (int i = 0; i < stations.length; i++) {
			if (i != LOAD && i != STORE)
				continue;
			for (int j = 0; j < stations[i].length; j++) {
				
				ReservationStation currentRS = stations[i][j];
				
				if (currentRS.finished() && currentRS.busy) {
					currentRS.busy = false;
					return currentRS;
				}
			}
		}
		return null;
	}
	
	public boolean areAllEmpty() {
		for (int i = 0; i < stations.length; i++) {
			for (int j = 0; j < stations[i].length; j++) {
				if (stations[i][j].busy)
					return false;
			}
		}
		
		return true;
	}
}
