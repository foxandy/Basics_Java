import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
Authors: Monsu Mathew, Andy Fox
Purpose: This program manages data entry and queries for a Flight Planner database.
		 An input file that contains a list of flights' origins, destinations, and numbers as a reference.
		 The program then assigns passengers for a flight based on user-entered information.
General Design: 
				main - manages initialization and overall flow of the program
				initializeFiles - as a form of exception handling, allows the user to specify the filepath for the
								  input/reference file, reservations files, and a directory for any saved output
				loadReservationData - stores dynamic data for current Reserved Flights in arrays; can manage up to 100 users
				loadFlightData - stores reference data for Flights in arrays; can handle up to 100 flights
				newReservation - collects user input for Passenger Name, Origin, and Destination, then looks up the
								 corresponding Flight Number, and finally adds the matched data to the dynamic Reservation Data;
								 NOTE: will not add a duplicate Passenger-Flight record
				showAllData - displays all existing Reservation data on the console
				searchCustomer - displays all existing Reservation data for a particular passenger, along with writing this to a file;
				searchFlight - displays all existing passengers on a particular Flight, along with writing this to a file
*/

public class flightPlanner {
/**
 * This is the main method that exposes exposes the user to the their choices in Flight Planner.
 * 
 * @throws FileNotFoundException
 */

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		//String defaultReservation = "\\reservedFlights.txt";
		//String defaultFlights = "\\allFlights.txt";
		System.out.println("Welcome to the Flight Planner");
		
		//defining the file paths and names
		String reservedFlightsFile = initializeFiles("reserved", in);
		String flightReferenceFile = initializeFiles("reference", in);
		String directory = initializeFiles("directory", in);
		
		//array definition
		final int ARRAY_LENGTH = 100;
		boolean repeat = true;
		String[] names = new String[ARRAY_LENGTH];
		String[] origins = new String[ARRAY_LENGTH];
		String[] destinations = new String[ARRAY_LENGTH];
		int flights[] = new int[ARRAY_LENGTH];
		
		//this portion will repeat until the user enters 'Q' to quit the program
		while (repeat){
			int reservationCount = loadReservationData(names, origins, destinations, flights, reservedFlightsFile);
			System.out.println("Please Select From One of the Following:\n"
							   + "N - in order to create a new flight reservation;\n"
							   + "S - in order to show all the data for the existing reservation for all the users;\n"
							   + "U - in order to show all the reservations for a particular user;\n"
							   + "B - in order to show the names of the customers who have bookings for a particular flight;\n"
							   + "Q - in order to quit this application");
			
			String response = in.nextLine().toUpperCase();
			if(response.equals("N")){
				newReservation(names, origins, destinations, flights, reservationCount, 
							   flightReferenceFile, reservedFlightsFile, in);
			}
			else if(response.equals("S")){
				showAllData(names, origins, destinations, flights, reservationCount);
			}
			else if(response.equals("U")){
				searchCustomer(names, origins, destinations, flights, reservationCount, directory, in);
			}
			else if(response.equals("B")){
				searchFlight(names, origins, destinations, flights, reservationCount, directory, in);
				in.nextLine();
			}
			else if(response.equals("Q")){
				repeat = false;
				System.out.println("Thank you!");
			}
			else System.out.println("Please enter a valid selection.");
		}
		in.close();
	}
/**
 * This method will get the file paths the program will access to store and manipulate files.	
 * @param fileType - the type of file that is being passed 
 * @param in - scanner variable
 * @return filePath - the file path the program should use for the respective file
 */
	public static String initializeFiles(String fileType, Scanner in){
		String filePath = "";
		int valid = 0;
		if(fileType.equals("reserved")){
			//filePath = "C:\\Users\\Andrew\\Desktop\\reservedFlights.txt";
			System.out.println("The directory for the Current Reservations file is the default location");
			filePath = "reservedFlights.txt";
		}
		else if(fileType.equals("reference")){
			//filePath = "C:\\Users\\Andrew\\Desktop\\allFlights.txt";
			System.out.println("The directory for the Flight Reference file is the default location");
			filePath = "allFlights.txt";
		}
		else if(fileType.equals("directory")){
			//filePath = "C:\\Users\\Andrew\\Desktop";
			System.out.println("The directory for file output is the default location");
			filePath = "Output ";
		}
		while (valid == 0){
			System.out.println("Would you like to change the current file path?  Type Y or N.");
			String choice = in.nextLine();
		
			if(choice.toUpperCase().equals("Y")){
				System.out.println("Please enter a new file path.");
				filePath = in.nextLine();
				filePath = filePath +"\\";
				valid = 1;
			}else if (choice.toUpperCase().equals("N")){
				valid = 1;
			}else{
				System.out.println("Please enter either a Y or N");
			}
		}		
		return filePath;
	}
/**
 * This method will extract the data from the reservation file and populate the arrays
 * @param names - the array which holds the passenger names
 * @param origins - the array which holds the origin city
 * @param destinations - the array which holds the destination city
 * @param flights - the array which holds the flight number
 * @param reservedFlights - the file with reservation data
 * @return - the number of reservations in the reservation file
 * @throws FileNotFoundException
 */
	public static int loadReservationData(String[] names, String[] origins, String[] destinations, 
			   int flights[], String reservedFlights) throws FileNotFoundException {
		int counter = 0;
		try{
			File inputFile = new File(reservedFlights);
			Scanner in = new Scanner(inputFile);
			while(in.hasNextLine()){
				String line = in.nextLine();
				Scanner lineScanner = new Scanner(line);
				lineScanner.useDelimiter(";");
				
				names[counter] = lineScanner.next();
				origins[counter] = lineScanner.next();
				destinations[counter] = lineScanner.next();
				String flight = lineScanner.next();
				flights[counter] = Integer.parseInt(flight);
				counter++;
				lineScanner.close();
			}
			in.close();
		}
		catch (FileNotFoundException exception){
			System.out.println("Warning!  Initial Reservation file not found.\n");
			counter = 0;
		}
		return counter;
	}
/**
 * This method will extract the data from the flight reference file and populate the arrays
 * @param origins - origin cities
 * @param destinations - destination cities
 * @param flights - flight numbers
 * @param flightReferenceFile - data with flight routes
 * @return - number of flight routes in the file
 * @throws FileNotFoundException
 */
	public static int loadFlightData(ArrayList<String> origins, ArrayList<String> destinations, ArrayList<Integer> flights,
				      String flightReferenceFile) throws FileNotFoundException {
		int counter = 0;
		try{
			File inputFile = new File(flightReferenceFile);
			Scanner in = new Scanner(inputFile);
			while(in.hasNextLine()){
				String line = in.nextLine();
				Scanner lineScanner = new Scanner(line);
				lineScanner.useDelimiter(";");
				

				origins.add(lineScanner.next());
				destinations.add(lineScanner.next());
				String flight = lineScanner.next();
				flights.add(Integer.parseInt(flight));
				counter++;
				lineScanner.close();
			}
			in.close();
		}
		catch (FileNotFoundException exception){
			System.out.println("Warning!  Reference file not found.");
			counter = 0;
		}
		return counter;	
	}

/**
 * This method will allow the user to input a new reservation into the system	
 * @param names - names of all the passengers
 * @param origins - corresponding passenger origin
 * @param destinations - corresponding passenger destination
 * @param flights - corresponding passenger flight
 * @param counter - number of entries in the reservation file
 * @param flightReferenceFile - all the potential flight routes
 * @param reservedFlightFile - all current reservations
 * @param in - scanner variable
 * @throws FileNotFoundException
 */
	public static void newReservation(String[] names, String[] origins, String[] destinations,
									  int flights[], int counter, String flightReferenceFile, 
									  String reservedFlightFile, Scanner in) throws FileNotFoundException{

		ArrayList<String> allOrigins_list = new ArrayList<String>();
		ArrayList<String> allDestinations_list = new ArrayList<String>();
		ArrayList<Integer> allFlights_list = new ArrayList<Integer>();
		int match = 0;
		int duplicate = 0;
		int j = 0;
		int k = 0;
		int flightCount = loadFlightData(allOrigins_list, allDestinations_list, allFlights_list, flightReferenceFile);
		String[] allOrigins = new String[flightCount];
		String[] allDestinations = new String[flightCount];
		int allFlights[] = new int[flightCount];
		
		//convert ArrayList to Arrays for program usage
		for (int x=0;x<flightCount;x++){
			allOrigins[x]=allOrigins_list.get(x);
			allDestinations[x] = allDestinations_list.get(x);
			allFlights[x] = allFlights_list.get(x);
		}
		
		System.out.println("Enter reservation name (first and last): ");
		String name = in.nextLine();
		System.out.println("Enter origin city: ");
		String origin = in.nextLine();
		System.out.println("Enter destination city: ");
		String destination = in.nextLine();
		
		while (match == 0 && j < flightCount){
			if(origin.toUpperCase().equals(allOrigins[j].toUpperCase()) && destination.toUpperCase().equals(allDestinations[j].toUpperCase())){
				while (duplicate == 0 && k < counter){
					if(name.toUpperCase().equals(names[k].toUpperCase()) && allFlights[j] == flights[k])
						duplicate = 1;
					k++;
				}
				if(duplicate == 1){
					match = 1;
					System.out.println("This flight is already in the database for this user.");
				}
				else{
					names[counter] = name;
					origins[counter] = origin;
					destinations[counter] = destination;
					flights[counter] = allFlights[j];
					counter++;
					match = 1;
				}
			}
			j++;
		}
		if (match == 0){
			System.out.println("Unfortunately this origin and destination pair does not exist.  Please try again.");
		}
		if (match == 1 && duplicate == 0){
			PrintWriter out = new PrintWriter(reservedFlightFile);
			for(int i = 0; i < counter; i++){
				out.print(names[i]+";"+origins[i]+";"+destinations[i]+";"+flights[i]);
				out.println();
			}
			System.out.println("Flight for "+ name + " added successfully!");
			out.close();
		}
	}

/**
 * This method will show all the data in the current reservation file to the console
 * @param names - names of all the passengers
 * @param origins - corresponding passenger origin
 * @param destinations - corresponding passenger destination
 * @param flights - corresponding passenger flight
 * @param counter - number of entries in the reservation file
 * @throws FileNotFoundException
 */
	public static void showAllData(String[] names, String[] origins, String[] destinations, 
					   int flights[], int counter) throws FileNotFoundException {
		System.out.printf("%-26s%-20s%-20s%-6s", "Name", "Origin", "Destination","Flight");
		System.out.println();
		for(int i = 0; i < counter; i++){
			System.out.printf("%-26s%-20s%-20s%-6d", names[i], origins[i], destinations[i], flights[i]);
			System.out.println();
		}
	}

/**
 * This method will allow the user to search for a customer and display/output their current reservation(s)	
 * @param names - names of all the passengers
 * @param origins - corresponding passenger origin
 * @param destinations - corresponding passenger destination
 * @param flights - corresponding passenger flight
 * @param counter - number of entries in the reservation file
 * @param directory - the file path for the output file
 * @param in - scanner variable
 * @throws FileNotFoundException
 */
	public static void searchCustomer(String[] names, String[] origins, String[] destinations, 
								      int[] flights, int counter, String directory, Scanner in) throws FileNotFoundException
	{
		System.out.println("Please enter name of passenger/customer to search for:");
		String inputName = in.nextLine();
		try{
			int j=0;
			PrintWriter out = new PrintWriter(directory+inputName+" Search.txt");
			for(int i = 0;i<counter;i++){
				if (inputName.toUpperCase().equals(names[i].toUpperCase())){
					if (j==0){
						out.printf("%-16s%-16s%-16s%-6s", "Name", "Origin", "Destination","Flight");
						out.println();
						System.out.printf("%-16s%-16s%-16s%-6s", "Name", "Origin", "Destination","Flight");
						System.out.println();
						j++;
					}
					out.printf("%-16s%-16s%-16s%-6d", names[i], origins[i], destinations[i], flights[i]);
					out.println();
					System.out.printf("%-16s%-16s%-16s%-6d", names[i], origins[i], destinations[i], flights[i]);
					System.out.println();
				}
			}
			System.out.println("Customer file printed.");
			System.out.println();
			out.close();
		}	
		catch (FileNotFoundException exception){
			System.out.println("Warning!  File path provided is invalid.");
		}
	}

/**
 * This method will allow the user to search for a flight number and display all the passengers on the flight	
 * @param names - names of all the passengers
 * @param origins - corresponding passenger origin
 * @param destinations - corresponding passenger destination
 * @param flights - corresponding passenger flight
 * @param counter - number of entries in the reservation file
 * @param directory - the output file path for the resulting file
 * @param in - scanner variable
 * @throws FileNotFoundException
 */
	public static void searchFlight(String[] names, String[] origins, String[] destinations, 
				       int[] flights, int counter, String directory, Scanner in) throws FileNotFoundException
	{
		int valid = 0;
		int inputNum =0;
		while (valid ==0){
			System.out.println("Please enter flight number:");
			try{
				if (in.hasNextInt()){	
					inputNum = in.nextInt();
					valid = 1;
				}else{
					System.out.println("Entry must be valid numbers.");
					in.next();
				}
			}
			catch (InputMismatchException exception){
				System.out.println("Entry must be valid numbers.");
				in.next();
			}
		}
		try{
			int j=0;
			PrintWriter out = new PrintWriter(directory+" Flight "+inputNum+" Search.txt");
			for(int i = 0;i<counter;i++){
				if (inputNum == flights[i]){
					if (j==0){
						out.printf("Passengers going from %1s to %1s on flight %1d are:", origins[i], destinations[i],inputNum);
						out.println();
						System.out.printf("Passengers going from %1s to %1s on flight %1d are:", origins[i], destinations[i],inputNum);
						System.out.println();
						j++;
					}
					out.printf("%-26s", names[i]);
					out.println();
					System.out.printf("%-26s", names[i]);
					System.out.println();
				}
			}
			System.out.println("Flight file printed.");
			System.out.println();
			out.close();
		}
		catch (FileNotFoundException exception){
			System.out.println("Warning!  File path provided is invalid.");
		}
		catch (IllegalArgumentException exception){
			System.out.println("Illegal entry.");
		}
	}
}
