package mainMemory;

public class InputData {
	
	byte data;
	
	short address;
	
	InputData(byte data, String hexAddress) {
		this.data = data;
		this.address = (short) (Integer.parseInt(hexAddress, 16));
	}
	
}
