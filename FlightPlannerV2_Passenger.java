package project4;

public class Passenger {
	private String firstName;
	private String lastName;
	private String fullName;
	
	private Flight flightDetails;
	
	public Passenger(String first, String last, Flight myFlight){
		firstName = first;
		lastName = last;
		fullName = first + " " + last;
		flightDetails = myFlight;
	}
	
	public Passenger(){
		firstName = "";
		lastName = "";
		fullName = firstName + " " + lastName;
		flightDetails = new Flight();
	}
	
	public String getName(){
		return fullName;
	}
	
	public String getOrigin(){
		return flightDetails.getOrigin();
	}
	
	public String getDestination(){
		return flightDetails.getDestination();
	}
	
	public int getFlight(){
		return flightDetails.getFlightNum();
	}
}
