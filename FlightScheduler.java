import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.DayOfWeek;
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
import java.util.Comparator;
import java.text.DecimalFormat;

public class FlightScheduler {

    private static FlightScheduler instance;

    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public static FlightScheduler getInstance() {
        return instance;
    }   


    public FlightScheduler(String[] args) {}

    public void run() {
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.

        // START YOUR CODE HERE
        FlightLocationDataHolder data = new FlightLocationDataHolder();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("User: ");
            String input = scanner.nextLine();
            if(input.trim().toLowerCase().equals("exit")){
                System.out.println("Application closed.");
                break;
            }
            validateInput(input,data); //TODO
            System.out.println();
        }
    }

    private void validateInput(String input, FlightLocationDataHolder data) {
        //if(input == null)
        //return;
        String[] commands = input.split(" ");
        if(commands[0].toLowerCase().equals("schedule")) {
            if (commands.length < 2) {
                System.out.println("This location does not exist in the system.");
            }
            else {
                data.printFlightsBySchedule1(commands[1]);
            }
        }
        if(commands[0].toLowerCase().equals("departures")) {
            if (commands.length < 2) {
                System.out.println("This location does not exist in the system.");

            }
            Location location = data.findLocation(commands[1]);
            if (location == null)
            {
                System.out.println("This location does not exist in the system.");
            }
            else {
                Collections.sort(location.departures);
                data.printFlightsBySchedule(location.departures, location);
            }
        }
        if(commands[0].toLowerCase().equals("arrivals")) {
            if (commands.length < 2) {
                System.out.println("This location does not exist in the system.");

            }
            Location location = data.findLocation(commands[1]);
            if (location == null) {
                System.out.println("This location does not exist in the system.");
            }
            else {
                Collections.sort(location.arrivals, (flight1, flight2) -> {

                    if (flight1.arrivalDay != flight2.arrivalDay)
                        return flight1.arrivalDay.compareTo(flight2.arrivalDay);
                    return flight1.timeOfArrival.compareTo(flight2.timeOfArrival);
                });
                data.printFlightsBySchedule(location.arrivals, location);
            }
        }
        else if (commands[0].toLowerCase().equals("flights")) {
            data.printAllFlights();
        }
        else if(commands[0].toLowerCase().equals("locations")) {
            data.printAllLocations();
        }
        else if(commands[0].toLowerCase().equals("travel")) {
            if (commands.length < 3) {
                System.out.println("Usage: TRAVEL <from> <to> [cost/duration/stopovers/layover/flight_time]");
                return;
            }
            String from = commands[1];
            String to = commands[2];
            int x = 0;
            String sort = "duration";
            if (commands.length >= 4) {
                sort = commands[3];
            }


            if (commands.length == 5) {
                x = Integer.parseInt(commands[4]);
                //System.out.print(" n = " +  commands[4]+" , ");
            }

            Location source = data.findLocation(from);
            Location destination = data.findLocation(to);
            if (source == null) {
                System.out.println("Starting location not found.");
                return;
            }
            if (destination == null) {
                System.out.println("Ending location not found.");
                return;
            }
            Queue<List<Flight>> pathWay = new ArrayDeque<>();
            for (Flight flight : source.departures) {
                pathWay.add(new ArrayList<>(Arrays.asList(flight)));
            }
            List<List<Flight>> endPath = new ArrayList<>();
            while (!pathWay.isEmpty())
            {
                List<Flight> presentPath = pathWay.poll();
                if (presentPath.get(presentPath.size() - 1).destination == destination)
                {
                    endPath.add(presentPath);
                }
                else
                    {
                    if (presentPath.size() < 4)
                    {
                        List<Flight> departureFlights = presentPath.get(presentPath.size() - 1).destination.departures;
                        for (Flight flight : departureFlights)
                        {
                            List<Flight> newRoute = new ArrayList<>(presentPath);
                            newRoute.add(flight);
                            pathWay.offer(newRoute);
                        }
                    }
                }
            }
            if (endPath.size() == 0)
            {
                System.out.println("Sorry, no flights with 3 or less stopovers are available from " +
                        source.name + " to " + destination.name + ".");
                return;
            }
            if (sort.toLowerCase().equals("stopovers"))
            {
                Collections.sort(endPath, (list1, list2) -> list1.size() - list2.size());
            }
            else if (sort.toLowerCase().equals("flight_time"))
            {
                Collections.sort(endPath, Comparator.comparingInt(list -> list.stream().
                        mapToInt(Flight::getDuration).sum()));
            }
            else if (sort.toLowerCase().equals("cost"))
            {
                Collections.sort(endPath, (list1, list2) -> (int) (list1.stream().mapToDouble(fl -> fl.getTicketPrice()).sum()
                        - list2.stream().mapToDouble(fl -> fl.getTicketPrice()).sum()));
            }
            else if (sort.toLowerCase().equals("duration"))
            {
                Collections.sort(endPath, (flights1, flights2) ->
                        data.durationSum(flights1) - data.durationSum(flights2));
            }

            else if (sort.toLowerCase().equals("layover"))
            {
                Collections.sort(endPath, (flights1, flights2) ->
                        data.getLayoverTime(flights1) - data.getLayoverTime(flights2));
            }
            else
                {
                System.out.println("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
                return;
            }

            List<Flight> flightRecord = null;
            if (x >= 0 && x < endPath.size())
            {
                flightRecord = endPath.get(x);
            }
            else if (x < 0)
            {
                flightRecord = endPath.get(0);
            }
            else
                {
                flightRecord = endPath.get(endPath.size() - 1);
            }


            long totalMins = data.durationSum(flightRecord);
            totalMins = (int) totalMins;
            long hours = totalMins / 60;
            long remMins = totalMins % 60;
            String totalduration = hours + "h " + remMins + "m";
            double totalCost = flightRecord.stream().mapToDouble(Flight::getTicketPrice).sum();
            System.out.printf("%-18s%d\n", "Legs:  ", flightRecord.size());
            System.out.printf("%-18s%s\n", "Total Duration:", totalduration);
            System.out.printf("%-18s$%.2f\n", "Total Cost:", totalCost);
            data.printTravel(flightRecord);
        }
        switch (commands[0].toLowerCase()) {
            case "location":
                if(commands.length > 1) {
                    if(commands[1].toLowerCase().equals("add"))
                    {
                        if (commands.length != 6) {
                            System.out.println("Usage:   LOCATION ADD <name> <lat> <long> <demand_coefficient>\n"
                                    + "Example: LOCATION ADD Sydney -33.847927 150.651786 0.2");

                        } else
                            {
                            String locationName = commands[2];
                            double latitude = Double.parseDouble(commands[3]);
                            double longitude = Double.parseDouble(commands[4]);
                            double demand = Double.parseDouble(commands[5]);

                            Location location = new Location(locationName, latitude, longitude, demand);
                            System.out.println(data.addLocation(location));
                        }
                    }
                    else if (commands[1].toLowerCase().equals("import")) {
                        if (commands.length < 3)
                        {
                            System.out.println("Error reading file.");
                            break;
                        }
                        List<String> imports = null;
                        String route = commands[2];
                        try {
                            imports = Files.lines(Paths.get(route)).map(loc -> loc.split(",")).map(loc -> locationVerification(loc)).map(loc -> data.addLocation(loc)).collect(Collectors.toList());
                        } catch (IOException e)
                        {
                            System.out.println("Error reading file.");
                        }
                        if (imports != null)
                        {
                            List<String> num = imports.stream().filter(str -> str.startsWith("Success")).collect(Collectors.toList());
                            int failedLines = imports.size() - num.size();
                            System.out.println("Imported " + num.size() + " location" + ((num.size() > 1 || num.size() == 0) ? "s." : "."));
                            if (failedLines == 1) {
                            System.out.println("1 line was invalid.");
                        }
                            else if (failedLines >= 2) {
                                System.out.println(failedLines + " lines were invalid.");
                            }

                        }
                    }
                    switch (commands[1].toLowerCase()){
                        case "export":
                            if(commands.length != 3){
                                System.out.println("Error writing file.");
                                break;
                            }
                            try {
                                int numExportedLocations = data.exportLocations(commands[2]);
                                System.out.println("Exported " + numExportedLocations+ " locations.");
                            } catch (IOException e) {
                                System.out.println("Error writing file.");
                            }
                            break;

                        default:
                            if(commands.length == 2 && !(commands[1].toLowerCase().equals("add"))){
                                data.displayLocation(commands[1]);
                            }
                    }

                }
                else
                    {
                    System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD <name> <latitude> <longitude> <demand_coefficient>\n"
                            + "LOCATION IMPORT/EXPORT <filename>");
                }
                break;
            case "flight":
                if(commands.length > 1) {
                    switch (commands[1].toLowerCase()){
                        case "export":
                            if(commands.length != 3)
                            {
                                System.out.println("Error writing file.");
                            }
                            else {
                                try {

                                    int count = 0;
                                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(commands[2]));
                                    for(int i=0; i<data.flights.size(); i++)
                                    {
                                        Flight flight = data.flights.get(i);
                                        String line = String.format("%s %s,%s,%s,%d,%d",
                                                flight.departureDay.getDisplayName(TextStyle.FULL, Locale.getDefault()),flight.timeOfDeparture, flight.source.name, flight.destination.name,
                                                flight.capacity,flight.passengers);
                                        fileWriter.write(line);
                                        if(i != data.flights.size()-1) {
                                            fileWriter.newLine();
                                        }
                                        count++;
                                    }
                                    fileWriter.close();
                                    System.out.println("Exported " + count + " flights.");
                                }
                                catch (IOException e) {
                                    System.out.println("Error writing file.");
                                }
                            }
                            break;

                        case "add":
                            if(commands.length != 7){
                                System.out.println("Usage:   FLIGHT ADD <departure time> <from> <to> <capacity>\n" + "Example: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
                            }
                            else
                                {
                                try
                                {
                                    LocalTime departureTime = LocalTime.parse(commands[3], DateTimeFormatter.ofPattern("H:mm"));
                                    DayOfWeek day = DayOfWeek.valueOf(commands[2].toUpperCase());
                                    String destination = commands[5];
                                    String source = commands[4];
                                    String message = data.addFlight(day, departureTime, source, destination, commands[6],0);
                                    System.out.println(message);
                                } catch (NumberFormatException nfe)
                                {
                                    System.out.println("Invalid integer capacity.");
                                }
                                catch (IllegalArgumentException e){
                                    System.out.println("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
                                }
                                catch (DateTimeParseException dateException){
                                    System.out.println("Invalid departure time. Use the format <day_of_week> <hour:minute>, with 24h time.");
                                }
                            }
                            break;

                        case "import":
                            if(commands.length < 3){
                                System.out.println("Error reading file.");
                            }
                            else {
                                List<Boolean> imports = null;
                                String route = commands[2];
                                try {
                                    imports = Files.lines(Paths.get(route)).map(flight -> flight.split(",")).map(command -> {
                                                try {
                                                    LocalTime departureTime = LocalTime.parse(command[0].split(" ")[1], DateTimeFormatter.ofPattern("H:mm"));
                                                    DayOfWeek day = DayOfWeek.valueOf(command[0].split(" ")[0].toUpperCase());
                                                    String destination = command[2];
                                                    String source = command[1];
                                                    String message = data.addFlight(day, departureTime, source, destination, command[3],Integer.parseInt(command[4]));
                                                    if (message != null && message.startsWith("Success"))
                                                    {
                                                        return true;
                                                    }
                                                } catch (Exception nfe)
                                                {
                                                    return false;
                                                }
                                                return false;
                                            }).collect(Collectors.toList());
                                } catch (IOException e)
                                {
                                    System.out.println("Error reading file.");
                                }
                                if (imports != null)
                                {

                                    List<Boolean> num = imports.stream().filter(res -> res).collect(Collectors.toList());
                                    int failedLines = imports.size() - num.size();
                                    if (num.size() == 0 || num.size() > 1)
                                    {
                                        System.out.println("Imported " + num.size() + " flights.");
                                    }
                                    else {
                                        System.out.println("Imported " + num.size() + " flight.");
                                    }
                                    if ((failedLines) > 0)
                                    {
                                        if (failedLines == 1)
                                        {
                                            System.out.println("1 line was invalid.");
                                        } else if (failedLines >= 2)
                                            {
                                            System.out.println(failedLines + " lines were invalid.");
                                        }
                                    }
                                }
                            }
                            break;

                        default:
                            try {
                                int flightNumber = Integer.parseInt(commands[1]);
                                if (commands.length == 2) {
                                    data.displayFlight(flightNumber);
                                }
                                else
                                    {
                                    if (commands[2].toLowerCase().equals("book")) {
                                        int bookings = 1;
                                        if (commands.length == 4)
                                        {
                                            bookings = Integer.parseInt(commands[3]);
                                        }
                                        if(bookings <= -1)
                                        {
                                            System.out.println("Invalid number of passengers to book.");
                                            break;
                                        }
                                            //System.out.println();
                                        data.bookFlight(flightNumber, bookings);
                                    }
                                    else if (commands[2].toLowerCase().equals("reset"))
                                    {
                                        Flight flight = data.resetFlight(flightNumber);
                                        String departure = data.dayConvert(flight.departureDay);
                                        LocalTime leaving = flight.timeOfDeparture;
                                        String source = flight.source.name;
                                        String destination = flight.destination.name;
                                        System.out.println("Reset passengers booked to 0 for Flight " + flightNumber + ", " + departure + " " + leaving + " "+source + " --> " + destination + ".");
                                    }
                                    else if (commands[2].toLowerCase().equals("remove"))
                                    {
                                        Flight flight = data.removeFlight(flightNumber);
                                        String departure = data.dayConvert(flight.departureDay);
                                        LocalTime leaving = flight.timeOfDeparture;
                                        String source = flight.source.name;
                                        String destination = flight.destination.name;
                                        System.out.println("Removed Flight " + flightNumber + ", " + departure + " " + leaving + " " + source + " --> " + destination + ", from the flight schedule.");
                                    }
                                }
                            }
                            catch (NumberFormatException n)
                            {
                                System.out.println("Invalid Flight ID.");
                            }catch (Exception f)
                            {
                                System.out.println("Invalid Flight ID.");
                            }
                    }
                }
                else
                    {
                    System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\n" +
                            "FLIGHT ADD <departure time> <from> <to> <capacity>\n" +
                            "FLIGHT IMPORT/EXPORT <filename>");
                }
                break;

            case "help":
                System.out.println("FLIGHTS - list all available flights ordered by departure time, then departure location name");
                System.out.println("FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight");
                System.out.println("FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file");
                System.out.println("FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, capacity, passengers booked)");
                System.out.println("FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 passenger. If the given number of bookings is more than the remaining capacity, only accept bookings until the capacity is full.");
                System.out.println("FLIGHT <id> REMOVE - remove a flight from the schedule");
                System.out.println("FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.");
                System.out.println("");

                System.out.println("LOCATIONS - list all available locations in alphabetical order");
                System.out.println("LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location");
                System.out.println("LOCATION <name> - view details about a location (it's name, coordinates, demand coefficient)");
                System.out.println("LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file");
                System.out.println("SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart");
                System.out.println("DEPARTURES <location_name> - list all departing flights, in order of departure time");
                System.out.println("ARRIVALS <location_name> - list all arriving flights, in order of arrival time");
                System.out.println("");

                System.out.println("TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not provided, display the first one in the order. If n is larger than the number of flights available, display the last one in the ordering.");
                System.out.println("");

                System.out.println("can have other orderings:");
                System.out.println("TRAVEL <from> <to> cost - minimum current cost");
                System.out.println("TRAVEL <from> <to> duration - minimum total duration");
                System.out.println("TRAVEL <from> <to> stopovers - minimum stopovers");
                System.out.println("TRAVEL <from> <to> layover - minimum layover time");
                System.out.println("TRAVEL <from> <to> flight_time - minimum flight time");
                System.out.println("");
                System.out.println("HELP - outputs this help string.");
                System.out.println("EXIT - end the program.");
                break;
            default:
                if(!(commands[0].toLowerCase().equals("departures") || commands[0].toLowerCase().equals("arrivals") || commands[0].toLowerCase().equals("schedule") || commands[0].toLowerCase().equals("locations") || commands[0].toLowerCase().equals("flights") || commands[0].toLowerCase().equals("travel"))) {
                    System.out.println("Invalid command. Type 'help' for a list of commands.");
                }
                

        }
    }

    public Location locationVerification(String[] loc) {
        try {
            return new Location(loc[0], Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
        }
        catch (Exception ex){
            return null;
        }
    }
}


 class FlightLocationDataHolder
{
    List<Location> locations;
    List<Flight> flights;
    int testNumber = 0;

    public FlightLocationDataHolder()
    {
        this.locations = new ArrayList<>();
        this.flights = new ArrayList<>();
    }

    public String addLocation(Location location){
        String result = "Successfully added location " + location.name + ".";
        for(int i = 0; i < locations.size(); i++)
        {
            if(locations.get(i).name.toLowerCase().equals(location.name.toLowerCase()))
            {
                return("This location already exists.");

            }
        }

        if(location.lat < -85 || location.lat >85)
        {
            result = "Invalid latitude. It must be a number of degrees between -85 and +85.";
            return result;
        }

        else if(location.coefficient < -1 || location.coefficient > 1)
        {
            result = "Invalid demand coefficient. It must be a number between -1 and +1.";
            return result;
        }

        else if(location.lon < -180 || location.lon > 180)
        {
            result = "Invalid longitude. It must be a number of degrees between -180 and +180.";
            return result;
        }
        locations.add(location);
        return result;
    }



    public void printFlightsBySchedule(List<Flight> arrangedS, Location location) {
        String locationName = location.name;
        System.out.println(Character.toUpperCase(locationName.charAt(0)) + locationName.substring(1));
        for(int i = 0; i < 55; i++)
        {
            System.out.print("-");
        }
        System.out.println("");

        System.out.println("ID   Time        Departure/Arrival to/from Location");

        for(int i = 0; i < 55; i++)
        {
            System.out.print("-");
        }
        System.out.println("");

        if(arrangedS.size() == 0 )
        {
            System.out.println("(None)");
        }else
        {
            for(Flight flight: arrangedS) {
                String shownDay = null;
                LocalTime shownTime = null;
                String variableText;

                if(flight.source.name.toLowerCase().equals(locationName.toLowerCase()))
                {
                    variableText = "Departure to " + flight.destination.name;
                    shownTime = flight.timeOfDeparture;
                    shownDay = dayConvert(flight.departureDay);
                }
                else
                {
                    variableText = "Arrival from " + flight.source.name;
                    shownDay = dayConvert(flight.arrivalDay);
                    shownTime = flight.timeOfArrival;

                }

                System.out.format("%4s%10s   %12s%n", flight.ID, shownDay + " " + shownTime, variableText);

            }
            //System.out.println();
        }
    }
    public static String dayConvert(DayOfWeek day) {
        if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("monday")) {
            return "Mon";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("tuesday")) {
            return "Tue";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("wednesday")) {
            return "Wed";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("thursday")) {
            return "Thu";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("friday")) {
            return "Fri";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("saturday")) {
            return "Sat";

        } else if (day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toLowerCase().equals("sunday")) {
            return "Sun";

        }
        return null;
    }



    public Flight addFlight(Flight flight){
        if(flights.add(flight))
            return flight;
        return null;
    }

    public String addFlight(DayOfWeek dayOfWeek, LocalTime departureTime,
                            String source, String destination, String capacity, int alreadyBooked) {


        boolean found = false;
        //check if source is in locations List
        for(int i = 0; i < locations.size(); i++)
        {
            if(source.toLowerCase().equals(locations.get(i).name.toLowerCase()))
            {
                found = true;
                break;
            }
        }
        if(found == false)
        {
            return ("Invalid starting location.");

        }

        found = false;
        for(int i = 0; i < locations.size(); i++)
        {
            found = false;
            if(destination.toLowerCase().equals(locations.get(i).name.toLowerCase()))
            {
                found = true;
                break;
            }
        }

        if(found == false)
        {
            return ("Invalid ending location.");

        }

        if(Integer.parseInt(capacity) <= 0) {
            return "Invalid positive integer capacity.";
        }

        if(source.toLowerCase().equals(destination.toLowerCase()))
        {
            return("Source and destination cannot be the same place.");

        }
        Location locationFromObj = findLocation(source);
        Location locationToObj = findLocation(destination);
        int numCapacity = Integer.parseInt(capacity);
        Flight newFlight = new Flight(testNumber, departureTime, dayOfWeek, locationFromObj, locationToObj, numCapacity, alreadyBooked);
        String runwayArrivalAvailability = locationToObj.hasRunwayArrivalSpace(newFlight);
        String runwayDepartureAvailability = locationFromObj.hasRunwayDepartureSpace(newFlight);

        if(runwayDepartureAvailability != null) {
            return runwayDepartureAvailability;
        }

        if(runwayArrivalAvailability != null) {
            return runwayArrivalAvailability;
        }
        locationToObj.arrivals.add(newFlight);
        locationFromObj.departures.add(newFlight);
        if(addFlight(newFlight)!=null) {
            testNumber++;
            return "Successfully added Flight " + newFlight.ID + ".";
        }
        return null;
    }



    public Location findLocation(String word){
        Location location = null;
        for(Location loc:locations){
            if(word.toLowerCase().equals(loc.name.toLowerCase()))
            {
                location = loc;
                break;
            }
        }
        return location;
    }

    public Flight findFlight(int flightNumber) throws Exception {
        for(Flight flight:flights){
            if(flight.ID == flightNumber) {
                return flight;
            }
        }
        throw new Exception();
    }

    public void displayFlight(int flightNumber) {
        try {
            Flight flight = findFlight(flightNumber);
            System.out.format("%-7s%d\n", "Flight", flightNumber);
            System.out.format("%-14s%s\n", "Departure:", flight.departureDay.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    + " " + flight.timeOfDeparture + " " + flight.source.name);
            System.out.format("%-14s%s\n", "Arrival:", flight.arrivalDay.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    + " " + flight.timeOfArrival + " " + flight.destination.name);
            System.out.format("%-14s%,2d%s\n", "Distance:", Math.round(flight.distance), "km");
            System.out.format("%-14s%s\n", "Duration:", convertMinToHourMinFormat(flight.duration));
            System.out.format("%-14s%s%.2f\n", "Ticket Cost:", "$", flight.getTicketPrice());
            System.out.format("%-14s%s\n", "Passengers:", flight.passengers + "/" + flight.capacity);
        }
        catch (Exception e)
        {
            System.out.println("Invalid Flight ID.");

        }
    }

    private String convertMinToHourMinFormat(int mins) {
        int hours = mins/60;
        int remMins = mins%60;
        return hours + "h " + remMins + "m";
    }

    public void printAllLocations() {
        /*if(locations.size() == 0){
            System.out.println("(None)");
            return;
        }*/
        System.out.println("Locations (" + locations.size() + "):");
        if(locations.size()==0){
            System.out.println("(None)");
            return;
        }
        List<String> sortedLocationList = locations.stream().map(Location::getName)
                .sorted().collect(Collectors.toList());
        for(int i=0; i<sortedLocationList.size(); i++){
            System.out.print(sortedLocationList.get(i));
            if(i != sortedLocationList.size()-1)
                System.out.print(", ");
        }
        System.out.println();
    }

    public void printAllFlights() {
        if(flights.size() > 1)
            Collections.sort(flights);
        printFlights(flights);
    }

    private void printFlights(List<Flight> flights)
    {
        System.out.println("Flights");
        System.out.print("-------------------------------------------------------");
        System.out.println();
        System.out.format("%-5s%-12s%-12sSource --> Destination%n", "ID", "Departure", "Arrival");
        System.out.println("-------------------------------------------------------");
        // System.out.println();
        if(flights.size() == 0 ){
            System.out.format("%s\n","(None)");
        }else{
            for(Flight flight: flights) {
                System.out.format("%4s%10s%12s   %s",
                        flight.ID,
                        flight.departureDay.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + flight.timeOfDeparture,
                        flight.arrivalDay.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + flight.timeOfArrival,
                        flight.source.name + " --> " + flight.destination.name);
                System.out.println();
            }

        }
    }

    public void bookFlight(int flightNumber, Integer bookings) {
        try {
            Flight flight = findFlight(flightNumber);
            flight.book(bookings);
        }
        catch (Exception e)
        {
            System.out.println("Invalid Flight ID.");

        }
    }

    public Flight removeFlight(int flightNumber) {
        try {
            Flight flight = findFlight(flightNumber);
            flight.source.departures.remove(flight);
            flight.destination.arrivals.remove(flight);
            flights.remove(flight);
            return flight;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Flight resetFlight(int flightNumber) {
        try {
            Flight flight = findFlight(flightNumber);
            flight.reset();
            return flight;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public void displayLocation(String command) {
        Location location = findLocation(command);
        if(location != null){
            // System.out.println();
            System.out.format("%-13s%s\n","Location:",location.name);
            System.out.format("%-13s%f\n","Latitude:",location.lat);
            System.out.format("%-13s%f\n","Longitude:",location.lon);
            System.out.format("%-13s%+.4f\n","Demand:",location.coefficient);
        }else{
            System.out.println("Invalid location name.");
        }
    }




    public int exportLocations(String fileName) throws IOException {
        Collections.sort(locations, Comparator.comparing(Location::getName));
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
        int count = 0;
        for(int i=0; i<locations.size(); i++) {
            Location location = locations.get(i);
            String line = location.name +","
                    + fmt(location.lat)+","
                    + fmt(location.lon)+","
                    + fmt(location.coefficient);

            fileWriter.write(line);
            count++;
            if(i != locations.size()-1)
                fileWriter.newLine();
        }
        fileWriter.close();
        return count;
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%.1f",d);
        else
            return String.format("%s",d);
    }


    public void printFlightsByDeparture(String locationName) {
        Location location = findLocation(locationName);
        if(location == null){
            System.out.println("This location does not exist in the system.");
            return;
        }
        Collections.sort(location.departures);
        printFlights(location.departures);
    }


    public void printDeparture(String locationName) {
        Location location = findLocation(locationName);
        if(location == null){
            System.out.println("This location does not exist in the system.");
            return;
        }
        Collections.sort(location.departures);
        printFlightsBySchedule(location.departures, location);
    }

    public void printFlightsBySchedule1(String locationName) {
        Location location = findLocation(locationName);
        if(location == null){
            System.out.println("This location does not exist in the system.");
            return;
        }
        Collections.sort(location.departures);
        Collections.sort(location.arrivals, (flight1, flight2) ->{

            if(flight1.arrivalDay != flight2.arrivalDay)
                return flight1.arrivalDay.compareTo(flight2.arrivalDay);
            return flight1.timeOfArrival.compareTo(flight2.timeOfArrival);
        });

        List<Flight> arrangedS = new ArrayList<>();
        int i=0, j=0;
        while(i < location.departures.size() && j < location.arrivals.size()){
            int compareResult = compareArrivalAndDepartureFlight(location.departures.get(i),
                    location.arrivals.get(j));
            if(compareResult < 0){
                arrangedS.add(location.departures.get(i++));
            }else if(compareResult > 0){
                arrangedS.add(location.arrivals.get(j++));
            }else{
                arrangedS.add(location.departures.get(i++));
                arrangedS.add(location.arrivals.get(j++));
            }
        }
        int flag = 0; List<Flight> remaining = null;
        if(i < location.departures.size()){
            flag = i;
            remaining = location.departures;
        } else if(j < location.arrivals.size()){
            flag = j;
            remaining = location.arrivals;
        }

        if(remaining != null){
            while(flag < remaining.size()){
                arrangedS.add(remaining.get(flag++));
            }
        }

        printFlightsBySchedule(arrangedS, location);
    }

    public int compareArrivalAndDepartureFlight(Flight departureFlight, Flight arrivalFlight) {
        if(departureFlight.departureDay != arrivalFlight.arrivalDay)
            return departureFlight.departureDay.compareTo(arrivalFlight.arrivalDay);
        return departureFlight.timeOfDeparture.compareTo(arrivalFlight.timeOfArrival);
    }

    public void findSuitableRoute(String[] commands) {
    }

    public int getLayoverTime(List<Flight> flights){
        long totalLayoverDuration = 0;
        for(int i=1; i<flights.size(); i++){
            totalLayoverDuration += getDifferenceInMinutes(flights.get(i-1).arrivalDay,
                    flights.get(i-1).timeOfArrival, flights.get(i).departureDay,
                    flights.get(i).timeOfDeparture);
        }
        return (int)totalLayoverDuration;
    }

    public int durationSum(List<Flight> flights){
        long totalDuration = 0;
        for(int i=1; i<flights.size(); i++) {
            totalDuration += flights.get(i-1).duration;
            totalDuration += getDifferenceInMinutes(flights.get(i-1).arrivalDay, flights.get(i-1).timeOfArrival, flights.get(i).departureDay, flights.get(i).timeOfDeparture);
        }
        totalDuration += flights.get(flights.size()-1).duration;
        return (int)totalDuration;
    }
    public static long getDifferenceInMinutes(DayOfWeek day1, LocalTime time1, DayOfWeek day2, LocalTime time2){
        int dayDifference = day2.compareTo(day1);


        long timeDifference = Duration.between(time1, time2).toMinutes();

        long totalMinutesDifference = dayDifference * 1440 + timeDifference;
        if(totalMinutesDifference < 0)
            totalMinutesDifference += 10080;
        return totalMinutesDifference;
    }
    public void printTravel(List<Flight> flights) {
        for(int i = 0; i < 61; i++)
        {
            System.out.print("-");
        }
        System.out.println("");

        System.out.println("ID   Cost      Departure   Arrival     Source --> Destination");

        for(int i = 0; i < 61; i++)
        {
            System.out.print("-");
        }
        System.out.println("");
        if(flights.size() == 0 )
        {
            System.out.println("(None)");
        }
        else
        {
            for(int i=0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                if(i!=0){
                    Flight previousFlight = flights.get(i-1);
                    long layOverDuration = getDifferenceInMinutes(previousFlight.arrivalDay,
                            previousFlight.timeOfArrival, flight.departureDay, flight.timeOfDeparture);
                    layOverDuration = (int) layOverDuration;
                    long hours = layOverDuration / 60;
                    long remMins = layOverDuration % 60;
                    String layOverDurationStr = hours + "h " + remMins + "m";
                    System.out.format("LAYOVER " + layOverDurationStr + " at " + flight.source.name+ "\n");
                }
                String cost = String.format("%.2f", flight.getTicketPrice());
                String arrival = flight.arrivalDay.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                String departure = flight.departureDay.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                System.out.format("%4s%2s%8s%10s   %8s   %s%n",
                        flight.ID, "$", cost,
                        departure + " " + flight.timeOfDeparture,
                        arrival + " " + flight.timeOfArrival,
                        flight.source.name + " --> " + flight.destination.name);
            }
        }
    }

}




