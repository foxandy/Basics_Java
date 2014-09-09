package project4;

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
				loadReservationData - stores dynamic data for current Reserved Flights in an object of class Passenger; 
				loadFlightData - stores reference data for Flights in an object of class Flight;
				newReservation - collects user input for Passenger Name, Origin, and Destination, then looks up the
								 corresponding Flight Number, and finally adds the matched data to the dynamic Reservation Data;
								 NOTE: will not add a duplicate Passenger-Flight record
				showAllData - displays all existing Reservation data on the console
				searchCustomer - displays all existing Reservation data for a particular passenger, along with writing this to a file;
				searchFlight - displays all existing passengers on a particular Flight, along with writing this to a file
*/
public class flightPlanner {
	/**
	 * Main function which runs the Flight Planner application 
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Flight> allFlights = new ArrayList<Flight>();
		ArrayList<Passenger> allPassengers = new ArrayList<Passenger>();
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to the Flight Planner");
		
		//defining the file paths and names
		String reservedFlightsFile = "reservedFlights.txt";
		String flightReferenceFile = "allFlights.txt";

		boolean repeat = true;
		loadFlightData(flightReferenceFile, allFlights);
		loadReservationData(allPassengers, allFlights, reservedFlightsFile);
		
		//this portion will repeat until the user enters 'Q' to quit the program
		while (repeat){
			System.out.println("Please Select From One of the Following:\n"
							   + "N - in order to create a new flight reservation;\n"
							   + "S - in order to show all the data for the existing reservation for all the users;\n"
							   + "U - in order to show all the reservations for a particular user;\n"
							   + "B - in order to show the names of the customers who have bookings for a particular flight;\n"
							   + "Q - in order to quit this application");
			
			String response = in.nextLine().toUpperCase();
			if(response.equals("N")){
				newReservation(allFlights, allPassengers, flightReferenceFile, reservedFlightsFile, in);
			}
			else if(response.equals("S")){
				showAllData(allPassengers);
			}
			else if(response.equals("U")){
				searchCustomer(in, allPassengers);
			}
			else if(response.equals("B")){
				searchFlight(in, allPassengers);
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
	 * This method will extract the data from the reservation file and populate the arraylist of type Passenger
	 * @param allPassengers - arrayList of type Passenger, to store information about passengers
	 * @param allFlights - arrayList of type Flight which stores Origin, Destination, and flight number
	 * @param reservedFlights - file to check for existing reservations
	 * @throws FileNotFoundException
	 */
		public static void loadReservationData(ArrayList<Passenger> allPassengers, ArrayList<Flight> allFlights, String reservedFlights) throws FileNotFoundException {
			try{
				File inputFile = new File(reservedFlights);
				Scanner in = new Scanner(inputFile);
				while(in.hasNextLine()){
					String line = in.nextLine();
					Scanner lineScanner = new Scanner(line);
					lineScanner.useDelimiter(";");
					String fullName = lineScanner.next().trim();
					int nameSplit = 0;
					for(int i=0; i<fullName.length(); i++){
						if(Character.isWhitespace(fullName.charAt(i))&&nameSplit==0)
								nameSplit = i;
					}
					String firstName = fullName.substring(0,nameSplit);
					String lastName = fullName.substring(nameSplit + 1);
					lineScanner.next();
					lineScanner.next();
					String flightStr = lineScanner.next().trim();
					int flightNumber = Integer.parseInt(flightStr);
					
					boolean found = false;
					int i=0;
					while(i < allFlights.size() && !found){
						if(flightNumber==allFlights.get(i).getFlightNum()){
							Passenger thisPassenger = new Passenger(firstName, lastName, allFlights.get(i));
							allPassengers.add(thisPassenger);
							found = true;
						}
						i++;
					}
					lineScanner.close();
				}
				in.close();
			}
			catch (FileNotFoundException exception){
				System.out.println("Warning!  Initial Reservation file not found.\n");
			}
		}

/**
 * This method will extract the data from the flight reference file and populate the arrayList of type Flight
 * @param flightReferenceFile - the file where the origin, destination, and flight number are stored
 * @param allFlights - the arrayList of type Flight where each object holds origin, destination, and flight number
 * @throws FileNotFoundException
 */
		public static void loadFlightData(String flightReferenceFile, ArrayList<Flight> allFlights) throws FileNotFoundException {
			
			try{
				File inputFile = new File(flightReferenceFile);
				Scanner in = new Scanner(inputFile);
				while(in.hasNextLine()){
					String line = in.nextLine();
					Scanner lineScanner = new Scanner(line);
					lineScanner.useDelimiter(";");
					
		
					String origin = (lineScanner.next().trim());
					String destination = (lineScanner.next().trim());
					String flight = lineScanner.next();
					flight = flight.trim();
					int flight_num = (Integer.parseInt(flight));
					Flight thisFlight = new Flight(origin,destination,flight_num);
					allFlights.add(thisFlight);
					lineScanner.close();
				}
				in.close();
			}
			catch (FileNotFoundException exception){
				System.out.println("Warning!  Reference file not found.");
			}
		}

/**
 * This method will allow the user to input a new reservation into the system
 * @param allFlights - the arrayList of type Flight where each object holds origin, destination, and flight number
 * @param allPassengers - arrayList of type Passenger, which holds passenger data
 * @param flightReferenceFile - file where the reference flights are stored
 * @param reservedFlightFile - flights where new reservations are stored
 * @param in - Scanner class
 * @throws FileNotFoundException
 */
		public static void newReservation(ArrayList<Flight> allFlights, ArrayList<Passenger> allPassengers, String flightReferenceFile, String reservedFlightFile, Scanner in) 
						   throws FileNotFoundException{
			
			int flightCount = allFlights.size();
			int match = 0;
			int duplicate = 0;
			int j = 0;
			int k = 0;
			int nameSplit = 0;
			
			System.out.println("Enter reservation name (first and last): ");
			String name = in.nextLine();
			System.out.println("Enter origin city: ");
			String origin = in.nextLine();
			System.out.println("Enter destination city: ");
			String destination = in.nextLine();
			for(int i=0; i<name.length(); i++){
				if(Character.isWhitespace(name.charAt(i))&&nameSplit==0)
						nameSplit = i;
			}
			String firstName = name.substring(0,nameSplit);
			String lastName = name.substring(nameSplit + 1);
			
			while (match == 0 && j < flightCount){
				if(origin.toUpperCase().equals(allFlights.get(j).getOrigin().toUpperCase()) && destination.toUpperCase().equals(allFlights.get(j).getDestination().toUpperCase())){
					while (duplicate == 0 && k < allPassengers.size()){
						if(name.toUpperCase().equals(allPassengers.get(k).getName().toUpperCase()) && allFlights.get(j).getFlightNum() == allPassengers.get(k).getFlight())
							duplicate = 1;
						k++;
					}
					if(duplicate == 1){
						match = 1;
						System.out.println("This flight is already in the database for this user.");
					}
					else{
						Passenger thisPassenger = new Passenger(firstName, lastName, allFlights.get(j));
						allPassengers.add(thisPassenger);
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
				for(int i = 0; i < allPassengers.size(); i++){
					out.print(allPassengers.get(i).getName()+";"+allPassengers.get(i).getOrigin()+";"+allPassengers.get(i).getDestination()+";"+allPassengers.get(i).getFlight());
					out.println();
				}
				System.out.println("Flight for "+ name + " added successfully!");
				out.close();
			}
		}


/**
 * This method will show all the data in the current reservation file to the console
 * @param allPassengers - arrayList of type Passenger which holds all passenger information
 * @throws FileNotFoundException
 */
		public static void showAllData(ArrayList<Passenger> allPassengers) throws FileNotFoundException {
			System.out.printf("%-26s%-20s%-20s%-6s", "Name", "Origin", "Destination","Flight");
			System.out.println();
			for(int i = 0; i < allPassengers.size(); i++){
				System.out.printf("%-26s%-20s%-20s%-6d", allPassengers.get(i).getName(), allPassengers.get(i).getOrigin(), allPassengers.get(i).getDestination(), allPassengers.get(i).getFlight());
				System.out.println();
			}
		}

/**
 * This method will allow the user to search by customer name and displays (to console and output file) all customer reservation information
 * @param in - Scanner class
 * @param allPassengers - arrayList of type Passenger which holds all passenger information
 * @throws FileNotFoundException
 */
		public static void searchCustomer(Scanner in, ArrayList<Passenger> allPassengers ) throws FileNotFoundException
		{
			System.out.println("Please enter name of passenger/customer to search for:");
			String inputName = in.nextLine();
			try{
				int j=0;
				PrintWriter out = new PrintWriter(inputName+" Search.txt");
				for(int i = 0;i< allPassengers.size();i++){
					if (inputName.toUpperCase().equals(allPassengers.get(i).getName().toUpperCase())){
						if (j==0){
							out.printf("%-16s%-16s%-16s%-6s", "Name", "Origin", "Destination","Flight");
							out.println();
							System.out.printf("%-16s%-16s%-16s%-6s", "Name", "Origin", "Destination","Flight");
							System.out.println();
							j++;
						}
						out.printf("%-16s%-16s%-16s%-6d", allPassengers.get(i).getName(), allPassengers.get(i).getOrigin(), allPassengers.get(i).getDestination(), allPassengers.get(i).getFlight());
						out.println();
						System.out.printf("%-16s%-16s%-16s%-6d", allPassengers.get(i).getName(), allPassengers.get(i).getOrigin(), allPassengers.get(i).getDestination(), allPassengers.get(i).getFlight());
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
 * This method will allow the user to display all passengers on a certain flight, specified by flight number
 * @param in - Scanner Class
 * @param allPassengers - arrayList of type Passenger which holds all passenger information
 * @throws FileNotFoundException
 */
		public static void searchFlight(Scanner in, ArrayList<Passenger> allPassengers) throws FileNotFoundException
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
				PrintWriter out = new PrintWriter("Flight "+inputNum+" Search.txt");
				for(int i = 0;i<allPassengers.size();i++){
					if (inputNum == allPassengers.get(i).getFlight()){
						if (j==0){
							out.printf("Passengers going from %1s to %1s on flight %1d are:", allPassengers.get(i).getOrigin(), allPassengers.get(i).getDestination(),inputNum);
							out.println();
							System.out.printf("Passengers going from %1s to %1s on flight %1d are:", allPassengers.get(i).getOrigin(), allPassengers.get(i).getDestination(),inputNum);
							System.out.println();
							j++;
						}
						out.printf("%-26s", allPassengers.get(i).getName());
						out.println();
						System.out.printf("%-26s", allPassengers.get(i).getName());
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
