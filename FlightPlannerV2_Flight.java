package project4;

public class Flight {

	private int flightNum;
	private String origin;
	private String destination;
	
	public Flight(String givenOrigin, String givenDestination, int givenFlightNum){
		flightNum = givenFlightNum;
		origin = givenOrigin;
		destination = givenDestination;
	}
	
	public Flight(){
		flightNum = 0;
		origin = "";
		destination = "";
	}
	
	public int getFlightNum(){
		
		return flightNum;
	}
	
	public String getOrigin(){
		
		return origin; 
	}
	public String getDestination(){
		
		return destination;
	}
	
}
