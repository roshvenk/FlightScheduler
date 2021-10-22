import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Collections;
import java.util.List;
import java.time.format.TextStyle;
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.text.*;
import java.time.temporal.ChronoUnit;
import java.time.*;
import java.lang.*;
import java.time.LocalTime;
import java.util.Comparator;
import java.text.DecimalFormat;
import java.util.Locale;

class flightList implements Comparable<flightList>
{
	Flight flight;
	DayOfWeek d;
	LocalTime time;

	public flightList(Flight flight, DayOfWeek d, LocalTime time)
	{
		this.flight = flight;
		this.d = d;
		this.time = time;
	}

	public int compareTo(flightList diff)
	{
		if(!(d.equals(diff.d)))
		{
			return d.compareTo(diff.d);
		}
		else
			{

			return time.compareTo(diff.time);
		}
	}
}

public class Location
{
	List<Flight> arrivals;
	List<Flight> departures;
	double lat;
	double lon;
	 String name;
	 double coefficient;

	public Location(String name, double lat, double lon, double coefficient)
	{
		arrivals = new ArrayList<>();
		departures = new ArrayList<>();
		this.lat = lat;
		this.name = name;
		this.coefficient = coefficient;
		this.lon = lon;
	}


	public String hasRunwayDepartureSpace(Flight f)
	{
		List<flightList> flights1 = new ArrayList<>();
		List<flightList> flights2 = new ArrayList<>();
		DayOfWeek day1 = f.departureDay;
		LocalTime time1 = f.timeOfDeparture;
		DayOfWeek day2 = day1;
		time1 = time1.minusHours(1);
		LocalTime time2 = time1.plusHours(1);

		if(time2.isBefore(time1))
		{
			day2 = day1.plus(1);
		}

		if(time1.isAfter(time1))
		{
			day1 = day1.minus(1);
		}
		for(Flight flight:arrivals)
		{
			if(timeCheck(flight.timeOfArrival, flight.arrivalDay, time1, day1, time2, day2))
			{
				flightList flightList = new flightList(flight, flight.arrivalDay, flight.timeOfArrival);
				if(dayClash(flight.arrivalDay, flight.timeOfArrival, day1, time1))
				{
					flights1.add(flightList);
				}
				else
				{
					flights2.add(flightList);
				}

			}
		}

		for(Flight flight:departures)
		{

			if(timeCheck(flight.timeOfDeparture, flight.departureDay, time1, day1, time2, day2))
			{
				flightList flightList = new flightList(flight, flight.departureDay, flight.timeOfDeparture);
				if(dayClash(flight.departureDay, flight.timeOfDeparture, day1, time1)){
					flights1.add(flightList);
				}
				else
					{
					flights2.add(flightList);
				}

			}
		}

		if(flights1.size() > 0)
		{
			Collections.sort(flights1);
			Flight flightClash = flights1.get(0).flight;
			int ID = flightClash.ID;
			String source = flightClash.source.name;
			String destination = flightClash.destination.name;
			String departure = flightClash.departureDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			String arrival = flightClash.arrivalDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			LocalTime timeD = flightClash.timeOfDeparture;
			LocalTime timeA = flightClash.timeOfArrival;
			if(flightClash.source.name.equals(name)){
				return "Scheduling conflict! This flight clashes with Flight " + ID + " departing from " + source + " on " + departure + " " + timeD + ".";
			}else{
				return "Scheduling conflict! This flight clashes with Flight " + ID + " arriving at " + destination + " on " + arrival + " " + timeA + ".";
			}
		}
		if(flights2.size() > 0) {
			Collections.sort(flights2);
			Flight flightClash = flights2.get(flights2.size()-1).flight;
			int ID = flightClash.ID;
			String source = flightClash.source.name;
			String destination = flightClash.destination.name;
			String departure = flightClash.departureDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			String arrival = flightClash.arrivalDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			LocalTime timeD = flightClash.timeOfDeparture;
			LocalTime timeA = flightClash.timeOfArrival;
			if(flightClash.source.name.equals(name)){
				return "Scheduling conflict! This flight clashes with Flight " + ID + " departing from " + source + " on " + departure + " " + timeD + ".";
			}else{
				return "Scheduling conflict! This flight clashes with Flight " + ID + " arriving at " + destination + " on " + arrival + " " + timeA + ".";
			}
		}
		return null;
	}

	static boolean dayClash(DayOfWeek day1, LocalTime time1, DayOfWeek day2, LocalTime time2)
	{
		if(day1.equals(day2)) {
			return time1.isAfter(time2);
		}
		else {
			return day1.compareTo(day2) >= 0;
		}
	}

	public static double distance(Location l1, Location l2) {
		double latdiff = l2.lat - l1.lat;
		latdiff = Math.toRadians(latdiff);
		double longdiff = l2.lon - l1.lon;
		longdiff = Math.toRadians(longdiff);
		double calculation = Math.pow(Math.sin(latdiff / 2), 2) +
				Math.pow(Math.sin(longdiff / 2), 2) *
						Math.cos(Math.toRadians(l1.lat)) *
						Math.cos(Math.toRadians(l2.lat));
		double finalresult = 2*Math.asin(Math.sqrt(calculation)) * 6371;

		return  finalresult;
	}


	public boolean timeCheck(LocalTime referenceTime, DayOfWeek referenceDay, LocalTime time1, DayOfWeek day1, LocalTime time2, DayOfWeek day2)
	{
		if(day1 == day2)
		{
			if(referenceDay != day1) {
				return false;
			}

			else {
				return referenceTime.isBefore(time2) && referenceTime.isAfter(time1);
			}
		}
		else
			{
				if(referenceDay == day2)
			{
				return referenceTime.isBefore(time2);
			}
			else if(referenceDay == day1)
			{
				return referenceTime.isAfter(time1);
			}
			else
				{
				return false;
			}
		}
	}

	public String hasRunwayArrivalSpace(Flight f)
	{
		List<flightList> flights1 = new ArrayList<>();
		List<flightList> flights2 = new ArrayList<>();
		DayOfWeek day1 = f.arrivalDay;
		LocalTime time1 = f.timeOfArrival;
		DayOfWeek day2 = day1;
		time1 = time1.minusHours(1);
		LocalTime time2 = time1.plusHours(1);

		if(time1.isAfter(time1)){
			day1 = day1.minus(1);
		}

		if(time2.isBefore(time1)){
			day2 = day1.plus(1);
		}

		for(Flight flight:arrivals)
		{
			if(timeCheck(flight.timeOfArrival, flight.arrivalDay, time1, day1, time2, day2))
			{
				flightList flightList = new flightList(flight, flight.arrivalDay, flight.timeOfArrival);
				if(dayClash(flight.arrivalDay, flight.timeOfArrival, day1, time1))
				{
					flights1.add(flightList);
				}
				else
				{
					flights2.add(flightList);
				}

			}
		}

		for(Flight flight:departures)
		{

			if(timeCheck(flight.timeOfDeparture, flight.departureDay, time1, day1, time2, day2))
			{
				flightList flightList = new flightList(flight, flight.departureDay, flight.timeOfDeparture);
				if(dayClash(flight.departureDay, flight.timeOfDeparture, day1, time1))
				{
					flights1.add(flightList);
				}else
					{
					flights2.add(flightList);
				}

			}
		}

		if(flights1.size() > 0) {
			Collections.sort(flights1);
			Flight flightClash = flights1.get(0).flight;
			int ID = flightClash.ID;
			String source = flightClash.source.name;
			String destination = flightClash.destination.name;
			String departure = flightClash.departureDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			String arrival = flightClash.arrivalDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			LocalTime timeD = flightClash.timeOfDeparture;
			LocalTime timeA = flightClash.timeOfArrival;
			if(flightClash.source.name.equals(name)){
				return "Scheduling conflict! This flight clashes with Flight " + ID + " departing from " + source + " on " + departure + " " + timeD + ".";
			}else{
				return "Scheduling conflict! This flight clashes with Flight " + ID + " arriving at " + destination + " on " + arrival + " " + timeA + ".";
			}
		}
		if(flights2.size() > 0) {
			Collections.sort(flights2);
			Flight flightClash = flights2.get(flights2.size()-1).flight;
			int ID = flightClash.ID;
			String source = flightClash.source.name;
			String destination = flightClash.destination.name;
			String departure = flightClash.departureDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			String arrival = flightClash.arrivalDay.getDisplayName(TextStyle.FULL, Locale.getDefault());
			LocalTime timeD = flightClash.timeOfDeparture;
			LocalTime timeA = flightClash.timeOfArrival;
			if(flightClash.source.name.equals(name)){
				return "Scheduling conflict! This flight clashes with Flight " + ID + " departing from " + source + " on " + departure + " " + timeD + ".";
			}else{
				return "Scheduling conflict! This flight clashes with Flight " + ID + " arriving at " + destination + " on " + arrival + " " + timeA + ".";
			}
		}
		return null;
    }
	public void addArrival(Flight f)
	{
		String arrivalRunway = hasRunwayArrivalSpace(f);
		if(arrivalRunway == null)
		{
			arrivals.add(f);
		}
	}

	public void addDeparture(Flight f)
	{
		String departureRunway = hasRunwayDepartureSpace(f);
		if(departureRunway == null)
		{
			departures.add(f);
		}
	}

	public String getName()
	{
		return name;
	}


}




