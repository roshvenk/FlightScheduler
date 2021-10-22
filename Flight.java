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


public class Flight implements Comparable<Flight>{

     int ID;
     LocalTime timeOfDeparture;
     DayOfWeek departureDay;
     Location source;
     Location destination;
     int capacity;
     int passengers;
     LocalTime timeOfArrival;
     DayOfWeek arrivalDay;
     int duration;
     double distance;


    public Flight(int ID, LocalTime timeOfDeparture, DayOfWeek departureDay, Location source, Location destination, int capacity, int alreadyBooked) {
        this.ID = ID;
        this.timeOfDeparture = timeOfDeparture;
        this.departureDay = departureDay;
        arrivalDay = departureDay;
        distance = Location.distance(source, destination);
        duration = (int)Math.round(distance/12);
        timeOfArrival = timeOfDeparture.plusMinutes(duration);
        if(!(timeOfDeparture.isBefore(timeOfArrival)))
        {
            arrivalDay = arrivalDay.plus(1);
        }

        this.source = source;
        this.destination = destination;
        this.capacity = capacity;
        this.passengers = alreadyBooked;


    }

    public int getDuration()
    {
        return duration;
    }

    //implement the ticket price formula
    public double getTicketPrice() {
        if((double) passengers/capacity <= 0.5) {
            return distance * (30 + (4 * (destination.coefficient - source.coefficient))) / 100 * (-0.4 * (double) passengers/capacity + 1);
        }else if((double) passengers/capacity>0.5 && (double) passengers/capacity <= 0.7) {
            return distance * (30 + 4 * ((destination.coefficient - source.coefficient))) / 100 * ((double) passengers/capacity + 0.3);
        }else{
            return distance * (30 + 4 * ((destination.coefficient - source.coefficient))) / 100 * ((0.2/Math.PI) * Math.atan(20 * (double) passengers/capacity - 14) + 1);

        }
    }
    public void reset()
    {
        passengers = 0;
    }

    public int compareTo(Flight f)
    {
        if(timeOfDeparture.compareTo(f.timeOfDeparture) != 0)
        {
            return timeOfDeparture.compareTo(f.timeOfDeparture);
        }
        else if(departureDay != f.departureDay)
        {
            return departureDay.compareTo(f.departureDay);
        }
        else
        {
            return source.name.compareTo(f.source.name);
        }
    }

    public boolean isFull()
    {
        if(passengers == capacity)
        {
            return true;
        }
        else
            return false;
    }

    public void book(int count)
    {
        double price = 0;
        int current = 0;
        for(int i=0; i<count; i++)
        {
            if(isFull())
            {
                break;
            }
            price += getTicketPrice();
            passengers ++;
            current ++;
        }
        price = Math.round(100.0 * price)/100.0;
        if(price == 0)
        {
            DecimalFormat f = new DecimalFormat("##.00");
            System.out.print("Booked " + current + " passengers on flight " + ID + " for a total cost of $0" );
            System.out.println(f.format(price));

        }
        else {
            DecimalFormat f = new DecimalFormat("##.00");
            System.out.print("Booked " + current + " passengers on flight " + ID + " for a total cost of $");
            System.out.println(f.format(price));
        }
        if(current < count)
        {
            System.out.println("Flight is now full.");
        }
        else if (isFull())
        {
            System.out.println("Flight is now full.");
        }

    }


    public static void main(String[] args)
    {

    }
}

