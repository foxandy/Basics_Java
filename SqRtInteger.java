import java.util.Scanner;

/**
Author: Andy Fox
Purpose: This program estimates the square root of a user-entered integer via the Babylonian method.  The Babylonian method
		 is iterative, and the program will iterate until a desired error bound is reached.
General Design: The main method manages primary inputs and outputs, as well as the iterations of the Babyloian method.
			    The countDigits method counts the number of digits in the user-entered integer and returns this count.
			    The initialValue method calculates/returns an initial estimate of the Babylonian method based on the # of digits.	
*/
public class Project1SqRt {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		int input_S = 0;
		String response = "";
		boolean perfSquare = false;
		
		do{	
				
			System.out.println("Enter the desired integer the square root of which should be calculated: ");
			
			//error check for valid data type for input integer
			if (!in.hasNextInt()) {
				System.out.println("Please enter an integer.");
				in.nextLine();
			}
			
			else {
				input_S = in.nextInt();
			
				if (input_S < 0) {
					System.out.println("Program cannot calcuate imaginary numbers.");
				}
				
				else {
					perfSquare = perfectSquare(input_S);

					if (perfSquare == false) {
						
						System.out.print("Enter the desired error bound between 0 and 1: ");
						
						//error check for valid data type for error
						if(!in.hasNextDouble()) {
							System.out.println("Please enter a valid number for error bound.");
							in.nextLine();
						}
						
						else {
							double error = in.nextDouble();

								//error check for an error bound between 0 and 1, exclusive
								if (error <= 0 || error >=1) {
									System.out.println("Please enter a valid number for error bound.");
								}
						
								else {
																
									//determine the number of digits of the user-entered S value
									int digits_d = 0;
									digits_d = countDigits(input_S);
										
									//determine initial value, x0, based on number of digits in user-entered S value
									double x0 = 1;
									x0 = initialValue(digits_d);
									
									//perform Babylonian method
									double x = x0;
									int iteration = 0;
									double errorCalc = 1;  //assumed must execute 1 iteration to ensure Math.sqrt is used only once
									while (errorCalc > error) {
										x = (x + (input_S/x)) / 2;
										errorCalc = x / Math.sqrt(input_S) - 1;
										iteration++;
									}
									
									System.out.printf("The estimate for the square root is: %.4f",x);
									System.out.println();
									System.out.printf("It takes %d iteration(s) to reach this estimate.", iteration);
									System.out.println();
								}	
							}
						}
					}
				}
			
			System.out.print("Would you like to estimate another square root (Y for yes, N for no)? ");
			response = in.next();
			
		}
		while(response.equals("Y"));
		in.close();
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
	
	public static boolean perfectSquare(int inputPar) {
		boolean perfSquare = false;
		for (int i = 0; i <= inputPar; i++) {
			if(i * i == inputPar) {
				System.out.println("Integer is a perfect square.  The square root is " + i + ".");
				perfSquare = true;
			}
		}
		if(perfSquare == true)
			return true;
		else
			return false;
		}
	}
