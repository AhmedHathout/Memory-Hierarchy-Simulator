package misc;

import java.util.Random;

public class AdditionalMathFunctions {
	
	/**
	 * Returns log num for the base 2
	 * @param num	Any integer number
	 * @return
	 */
	public static int log2(int num) {
		int i = 1;
		while (true) {
			if (num >= Math.pow(2, i - 1) && num < Math.pow(2, i))
				return i;
			i++;
		}
	}
	
	/**
	 * Takes the address that is to be given to the main memory and adjusts it to be 
	 * unsigned to make use of the last bit
	 * 
	 * @param address	The address that is to be given to the main memory
	 * @return
	 */
	public static int adjustAddress(short address) {
		int adjustedAddress = address;
		
		if (address < 0) {
			adjustedAddress += (int) (2 * Math.pow(2, 15));
		}
		return adjustedAddress;
	}
	
	/**
	 * Takes an integer and its number of bits and adjusts it to be signed with 
	 * these bits
	 * 
	 * @param immediate	Any given integer
	 * @param numOfBits	The number of bits that integer is supposed to have
	 * 
	 * @return	The adjusted integer
	 */
	public static int adjustImmediate(int immediate, int numOfBits) {
		int numOfBitsWithoutSign = numOfBits - 1;
		int maxNum = (int) (Math.pow(2, numOfBitsWithoutSign) - 1);
		if (immediate > maxNum)
			immediate -= 2 * (maxNum - 1);
		
		return immediate;
	}
	
	/**
	 * Generates any random number between the 2 given integers from (inclusive) 
	 * and to (exclusive)
	 * 
	 * @param from	The lower bound
	 * @param to	The higher bound
	 * 
	 * @return		The random number
	 */
	public static int generateRandomNumber(int from, int to) {
		return new Random().nextInt(to - from) + from;
	}

	/**
	 * Takes a 1D array of type byte and returns an integer (short) that is the 
	 * result of concatenating the first 2 bytes ONLY
	 * 
	 * @param bytes	A 1D byte array
	 * 
	 * @return the result of concatenating the first 2 bytes
	 */
	public static short byte2Short(byte[] bytes) {
		short result = bytes[1];
		result <<= 8;
		result |= (0x00ff) & (short) (bytes[0]);
		return result;
	}
	
}
