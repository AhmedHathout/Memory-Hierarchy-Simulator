package misc;

public class AdditionalMathFunctions {
	
	
	/**
	 * Returns log num for the base 2
	 * @param num
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
	
}
