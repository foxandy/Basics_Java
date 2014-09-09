import java.util.Arrays;
import java.util.Scanner;

/**
Author: Andy Fox
Purpose: This program estimates the square root of an array of user-entered integers via the Babylonian method.
		 The Babylonian method is iterative, and the program will iterate until a desired error bound is reached.
General Design: 
				main - Manages overall flow of the program and calls error-checking at the proper points
				collectInput - Creates the array of integers the user enters and whose square roots will be calculated;
							   NOTE: the array is dynamic and will increase size if a user enters >20 integers.
				errorCheck - Returns FALSE if all inputs are valid and the program is ready to run.
				babylonian - performs the Babylonian estimation algorithm one integer at a time, and displays the results as
						     per the user's specifications.
				countDigits - within the Babylonian method, counts the number of digits in the current integer and returns this count.
				intialValue - within the Babylonian method, calculcates and returns an initial estimate of the Babylonian 
						      method based on the # of digits.
*/

public class squareRootEstimation {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		final int LENGTH = 20;
		int[] inputs = new int[LENGTH];
		String inputMethod = "";
		String response = "";
		boolean errors = false;
		double errorBound = 0;
		
		do{
			
		System.out.println("What is your preferred method for entering integers (type 'one-at-a-time' or 'spaces')?");
		inputMethod = in.next();
		inputs = collectInput(inputs,inputMethod,in);
		
		System.out.println("Enter the desired error bound between 0 and 1: ");
		
		while(!in.hasNextDouble()) {
			System.out.print("Please enter a number between 0 and 1 for error:");
			in.next();
		}
		
		errorBound = in.nextDouble();
		
		errors = errorCheck(errorBound, inputs);
		if (errors == false){
			System.out.println();
			System.out.println("Entry is valid.  How would you like the results to be displayed (type 'columns' or 'rows')?");
			String results = in.next();
			System.out.println();
			
			if (results.equals("rows") || results.equals("columns")){
				for (int i = 0; i < inputs.length; i++){
					if (inputs[i] != 0){
						babylonian(inputs[i], errorBound, results);
					}
				}
			}
			else
				System.out.print("Please enter a valid choice for results display.");			
		}
		
		System.out.println();
		System.out.println("Would you like to estimate another set of square roots (Y for yes, N for no)? ");
		response = in.next();
		
		}while(response.equals("Y"));
		System.out.println("Thank you!");
		in.close();
	}
		
	public static int[] collectInput (int[] inputs, String inputMethod, Scanner in){
		int currentSize = 0;
		int tempInput = 0;
		
		if (inputMethod.equals("one-at-a-time")){
			System.out.println("Enter a list of integers one-at-a-time.  Type 'q' when finished.");
			while(!in.hasNext("[q]")){
				if(!in.hasNextInt()){
					System.out.println("Non-integers will not be calculated.");
					in.next();
				}
				else{
					tempInput = in.nextInt();
					if (tempInput > 0 && currentSize < inputs.length){
						inputs[currentSize] = tempInput;
						currentSize++;
					}
					else if (currentSize >= inputs.length){
						int[] newInputs = Arrays.copyOf(inputs, 2 * inputs.length);
						inputs = newInputs;
						inputs[currentSize] = tempInput;
						currentSize++;
					}
					else if (tempInput <= 0)
						System.out.println("Negative and zero numbers will not be collected");
				}
			}
			in.next();
		}
		else if (inputMethod.equals("spaces")){
			System.out.println("Enter a list of integers separated by spaces.  Type 'q' when finished.");
			while(!in.hasNext("[q]")){
				if(!in.hasNextInt()){
					System.out.println("Non-integers at will not be calculated.");
					in.next();
				}
				else{
					tempInput = in.nextInt();
					if (tempInput > 0 && currentSize < inputs.length){
						inputs[currentSize] = tempInput;
						currentSize++;
					}
					else if (currentSize >= inputs.length){
						int[] newInputs = Arrays.copyOf(inputs, 2 * inputs.length);
						inputs = newInputs;
						inputs[currentSize] = tempInput;
						currentSize++;
					}
					else if (tempInput <= 0)
						System.out.println("Negative and zero numbers will not be collected");
				}
			}
			in.next();
		}	
		else{
			System.out.println("Please enter a valid selection for method of input.");
		}
				
		System.out.println(currentSize + " integers collected!");
		System.out.println();
		
		
		return inputs;
	}
	
	public static boolean errorCheck(double errorBound, int[] inputs){
				
		double total = 0;
		for (double element : inputs){
			total = total + element;
		}
		
		if (total == 0){
			System.out.println("No values were collected  Please enter valid integers of which the square root should be calculated.");
			return true;
		}
		
		else if (errorBound <= 0 || errorBound >=1){
			System.out.println("Please enter a valid number for error bound.");
			return true;
		}
		
		else
			return false;
	}

	public static int countDigits(int inputPar) {
		boolean digitsComplete = false;
		inputPar = inputPar / 10;
		int digits = 0;
		
		while (!digitsComplete) {
			if (inputPar >= 1) {
				digits++;
				inputPar = inputPar / 10;
			}
			else {
				digits++;
				digitsComplete = true;
			}
		}
		return digits;
	}
	
	public static double initialValue(int digitPar) {
		double x0Par = 1;
		
		if (digitPar % 2 == 1) {
			int k = (digitPar - 1) / 2;
			
			//perform an exponent as a For loop instead of using the Math.pow function
			for (int i = 0; i < k; i++) {
				x0Par = x0Par * 10;
			}
			x0Par = 2 * x0Par;
		}
		else {
			int k = (digitPar - 2) / 2;
			
			for (int i = 0; i < k; i++) {
				x0Par = x0Par * 10;
			}
			x0Par = 6 * x0Par;
		}
		return x0Par;
	}
	
	public static void babylonian (int input, double errorBound, String results){
		int digits_d = countDigits(input);
		double x0 = initialValue(digits_d);
		double x = x0;
		int iteration = 0;
		double errorCalc = 1;
		while (errorCalc > errorBound) {
			x = (x + (input / x)) / 2;
			errorCalc = x / Math.sqrt(input) - 1;
			iteration++;
		}
		
		if (results.equals("columns")){
			System.out.printf("SqRt(" + input + ") = %.2f" + "  ",x);
		}
		else if (results.equals("rows")){
			System.out.printf("SqRt(" + input + ") = %.2f in " + iteration + " iterations.",x);
			System.out.println();
		}		
	}
}
