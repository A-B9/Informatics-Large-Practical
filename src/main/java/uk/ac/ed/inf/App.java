package uk.ac.ed.inf;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * This class is to be executed, it will calculate a flightpath for the Drone to deliver the orders for a
 * specific day. The user will input a string containing a date in the DD/MM/YYYY format, a port for the database
 * and a port for the webserver. Using this information, the application will retrieve all the orders for the given date
 * and the corresponding order details and generate a path to deliver as many of the orders as possible before the Drone
 * runs out of moves.
 * This application will calculate the percentage monetary value of all the orders that were delivered for that day and
 * generate a .geojson file for the flightpath for that day.
 *
 * @author Arbnor Bregu s1832263
 * @version 1.0
 * @date 02/12/2021
 */
public class App {
    private static Drone APPLETON_TOWER = new Drone(-3.186874, 55.944494);
    private static Derby derby;
    private static Menus menu;
    private static int moveCount = 1500;
    private static String webServerPort;
    private static NoFly nofly;


    public static void main(String[] args) throws SQLException, IOException {


        long startTime = System.currentTimeMillis();

        String day = args[0];
        String month = args[1];
        String year = args[2];
        webServerPort = args[3];
        String databaseServer = args[4];
        String date = year + "-" + month + "-" + day;

        //String databaseServer = "1527";
        //webServerPort = "80";
        //String date = "2023-12-28";

        derby = new Derby("derby", databaseServer);
        menu = new Menus("localhost", webServerPort);
        nofly = new NoFly("localhost", webServerPort);

        //get the complete Orders database and OrderDetails database
        ArrayList<Orders> orderTable = derby.readOrderData();
        ArrayList<Orders> todaysOrders = new ArrayList<>();

        //Filter the Orders database for a specific date
        for (Orders ord : orderTable) {
            if (date.equals(ord.getDeliveryDate())) {
                todaysOrders.add(ord);
            }
        }

        //Filter the OrderDetails database for a specific date
        ArrayList<OrderDetails> todaysOrderDetails = new ArrayList<>();
        for (Orders ord : todaysOrders) {
            String str = "";
            for (OrderDetails orde : derby.readOrderDetailsData()) {

                if (ord.getOrderNo().equals(orde.getOrderNo())) {
                    //separate each item with an @ symbol as it is not within any item string.
                    str = str + "" + orde.getItem() + "@";
                }
            }
            //add the order detail to the list of order details for the required date.
            todaysOrderDetails.add(new OrderDetails(ord.getOrderNo(), str));
        }


        //BEGIN THE PATH-FINDING ALGORITHM
        Drone current_pos = APPLETON_TOWER;
        int orderCount = 0;
        int totalDelivered = 0;
        int totalOrders = todaysOrders.size();

        while (orderCount < totalOrders) {
            //get the location of the Drone when it has arrived at the restaurant.
            Drone arrivedAtRestaurant = pickUpOrder(todaysOrderDetails.get(orderCount), current_pos);
            //checks whether the location is within the confinement area
            if (!arrivedAtRestaurant.isConfined()) {
                //if the location isn't within the confinement area it will proceed to the next order.
                orderCount += 1;
            } else {
                String deliverToLoc = ""; //an empty string to hold the What3Words address of the delivery
                for (Orders order : todaysOrders) { // for each order in the list of todays orders
                    //checks whether the order numbers match
                    if (order.getOrderNo().equals(todaysOrderDetails.get(orderCount).getOrderNo())) {
                        //retrieves the What3Words address for the delivery location
                        deliverToLoc = order.getDeliverTo();
                    }
                }
                //calculates the path for the drone and saves the final position of the drone once it has reached the customer
                current_pos = deliverOrder(todaysOrders, todaysOrderDetails.get(orderCount), deliverToLoc, arrivedAtRestaurant);
                if (!current_pos.isConfined()) {// if the location of the Drone is not within the confinement area
                    //go to the next order
                    orderCount += 1;

                } else {
                    if (moveCount <= 165) {// if there are less than 150 moves the Drone will travel back to Appleton Tower
                        System.out.println("DRONE RETURNING TO APPLETON TOWER");
                        returnToAppleton(current_pos);
                        current_pos = APPLETON_TOWER;
                        break;
                    }
                    totalDelivered += 1; //updates the total deliveries made
                    orderCount += 1; // updates the order count
                }
            }
        }
        if (current_pos.closeTo(APPLETON_TOWER)) {
            System.out.println("Final move count: " + moveCount);
            //CALCULATE THE PERCENTAGE MONETARY VALUE THE DELIVERIES
            if (totalDelivered == todaysOrders.size()) { //IF ALL THE DELIVERIES HAVE BEEN MADE
                int valueDelivered = 0;
                int totalValuePossible = 0;
                //CALCULATE THE TOTAL COST OF ALL THE DELIVERIES MADE
                for (Deliveries delivery : derby.readDeliveriesData()) {
                    valueDelivered += delivery.getCostInPence();
                }
                totalValuePossible += valueDelivered;
                System.out.println("Total orders for " + date + ": " + todaysOrders.size());
                System.out.println("Total orders delivered: " + totalDelivered);
                System.out.println("Percentage Monetary Value = " + (valueDelivered / totalValuePossible * 100) + "%");
            } else {//
                double valueDelivered = 0.0;
                double totalValuePossible = 0.0;

                //Calculate value of all deliveries made
                for (Deliveries delivery : derby.readDeliveriesData()) {
                    valueDelivered += delivery.getCostInPence();
                }
                //Calculate value of all possible deliveries
                for (OrderDetails order : todaysOrderDetails) {
                    String[] str = order.getItem().split("@");
                    totalValuePossible += menu.getDeliveryCost(str);
                }
                System.out.println("Total orders for " + date + ": " + todaysOrders.size());
                System.out.println("Total orders delivered: " + totalDelivered);
                System.out.println("Percentage Monetary Value = " + (valueDelivered / totalValuePossible) * 100 + "%");
            }
        } else {
            returnToAppleton(current_pos);

            System.out.println("Final move count: " + moveCount);
            //CALCULATE THE PERCENTAGE MONETARY VALUE THE DELIVERIES
            if (totalDelivered == todaysOrders.size()) { //IF ALL THE DELIVERIES HAVE BEEN MADE
                int valueDelivered = 0;
                int totalValuePossible = 0;
                //CALCULATE THE TOTAL COST OF ALL THE DELIVERIES MADE
                for (Deliveries delivery : derby.readDeliveriesData()) {
                    valueDelivered += delivery.getCostInPence();
                }
                totalValuePossible += valueDelivered;
                System.out.println("Total orders for " + date + ": " + orderCount);
                System.out.println("Total orders delivered: " + totalDelivered);
                System.out.println("Percentage Monetary Value = " + (valueDelivered / totalValuePossible * 100) + "%");
            } else {//
                double valueDelivered = 0.0;
                double totalValuePossible = 0.0;

                //Calculate value of all deliveries made
                for (Deliveries delivery : derby.readDeliveriesData()) {
                    valueDelivered += delivery.getCostInPence();
                }
                //Calculate value of all possible deliveries
                for (OrderDetails order : todaysOrderDetails) {
                    String[] str = order.getItem().split("@");
                    totalValuePossible += menu.getDeliveryCost(str);
                }
                System.out.println("Total orders for " + date + ": " + orderCount);
                System.out.println("Total orders delivered: " + totalDelivered);
                System.out.println("Percentage Monetary Value = " + (valueDelivered / totalValuePossible) * 100 + "%");
            }
        }


        //CREATE THE GeoJson FILE

        //retrieve the points to create a lineString
        ArrayList<Flightpath> flightData = derby.readFlightpathData();
        ArrayList<Drone> points = new ArrayList<>();
        for (Flightpath flight : flightData) {
            points.add(new Drone(flight.fromLongitude, flight.fromLatitude));
            points.add(new Drone(flight.toLongitude, flight.toLatitude));
        }

        WriteGJson gson = new WriteGJson(points);
        String hold = gson.toJson();
        FileWriter geoJson = new FileWriter("drone-" + date + ".geojson");
        geoJson.write(hold);
        geoJson.close();

        long finishTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (finishTime - startTime) / 1000.0 + " seconds");

    }

    /**
     * This function will calculate the flightpath for the Drone. It will take 2 parameters, 1 orderDetails object and
     * the current position of the drone as a Drone object. It will retrieve the locations of the restaurants it needs
     * to travel to, and from the Drones current position the algorithm will calculate all possible moves and choose the move
     * that reduces the distance to the restaurant location whilst staying within the confinement area and not crossing
     * through any no-fly zones.
     * If there are no moves that satisfy the conditions, a Drone object with junk values (-999.0, -999.0) will be
     * returned to indicate the Drone cannot make a move.
     * The function will add all successful moves for the Drone to the flightpath table in the Derby database.
     *
     * @param order       OrderDetails object, it will use the information in the "item" parameter to find the location of the restaurant
     *                    the item is located in.
     * @param current_pos Drone, the current position of the Drone.
     * @return a Drone object for the final position of the Drone once it is close to the location of the restaurant.
     * @throws SQLException if an error occurs with inserting the required parameters into the table an SQLException will be thrown.
     */

    public static Drone pickUpOrder(OrderDetails order, Drone current_pos) throws SQLException {
        //find the pick-up locations
        Set<Drone> restaurantToCollectFrom = new HashSet<>(); //will not allow duplicates of the restaurant locations
        String[] foodItems = order.getItem().split("@");
        for (int index = 0; index < foodItems.length; index++) {
            String locationOfPickUp = menu.getLocation(foodItems[index]);
            What3Words what3words_address = new What3Words("localhost", webServerPort, locationOfPickUp);
            restaurantToCollectFrom.add(what3words_address.getDestination());
        }
        for (Drone drone : restaurantToCollectFrom) {
            Drone pickUpLocation = drone;
            //if there are no valid moves, make the current_pos not confined, and therefore quit the pickup
            while (!current_pos.closeTo(pickUpLocation)) {
                if (!current_pos.isConfined()) {
                    break;
                } else {
                    SortedMap<Double, Drone> validMoves = new TreeMap<>(); // sortedmaps to hold the best move as the first key
                    SortedMap<Double, Integer> validAngles = new TreeMap<>();
                    for (int angle = 0; angle <= 350; angle += 10) { // calculates every move possible at the current location
                        Drone moveTo = current_pos.nextPosition(angle);
                        // makes sure the move is confined and doesn't intersect the no-fly zones
                        if (moveTo.isConfined() && !checkForIntersection(current_pos, moveTo)) {
                            validMoves.put(moveTo.distanceTo(pickUpLocation), moveTo);
                            validAngles.put(moveTo.distanceTo(pickUpLocation), angle);
                        }
                    }
                    //if there are no valid moves
                    if (validMoves.isEmpty()) {
                        // turn the current position into a junk value if there is valid move
                        current_pos = new Drone(-999.0, -999.0);
                        break;
                    } else {
                        //create a junk value Drone that will be used later to hold the co-ordinates for the best move
                        Drone bestMove = new Drone(0.0, 0.0);

                        if (validMoves.get(validMoves.firstKey()).isConfined()) {
                            //validMoves.get(validMoves.firstkey()) will get the co-ordinates of the best move for the Drone to take.
                            bestMove = validMoves.get(validMoves.firstKey());
                        }
                        //gets the angle associated with the best move
                        int bestAngle = validAngles.get(validAngles.firstKey());
                        //add the flightpath to the database
                        derby.addFlightPath(new Flightpath(order.getOrderNo(), current_pos.getLongitude(), current_pos.getLatitude(), bestAngle, bestMove.getLongitude(), bestMove.getLatitude()));
                        moveCount = moveCount - 1;
                        current_pos = bestMove; //update the current position of the drone
                    }
                }
            }
            if (current_pos.closeTo(pickUpLocation)) {
                current_pos = current_pos.nextPosition(-999);
                //adds the hover position for the Drone to the flightpath table in the database
                derby.addFlightPath(new Flightpath(order.getOrderNo(), current_pos.getLongitude(), current_pos.getLatitude(), -999, current_pos.getLongitude(), current_pos.getLatitude()));
                moveCount = moveCount - 1;
            }
        }
        return current_pos;
    }

    /**
     * This function will calculate the flightpath for the Drone. It will take 2 parameters, 1 orderDetails object and
     * the current position of the drone as a Drone object.
     * The function will retrieve the location of the customer and calculate the shortest route from the Drones current
     * position to the customer whilst making sure each move stays within the confinement zone, and does not intersect
     * through any no-fly zones.
     * If there are no moves that satisfy the conditions, a Drone object with junk values (-999.0, -999.0) will be
     * returned to indicate the Drone cannot make a move.
     * It will add all the successful moves to the flightpath table in the Derby database and all successful deliveries
     * to the deliveries table in the Derby database.
     *
     * @param todaysOrders is an ArrayList of Orders objects that will contain all the order for the input date
     * @param order        OrderDetails object that is the specific order that is being delivered
     * @param deliverToLoc String that contains the What3Words address for the delivery location
     * @param current_pos  Drone objects that contains the co-ordinates for the current position of the Drone.
     * @return Drone object for the final position of the Drone.
     * @throws SQLException if an error occurs when adding information to the deliveries or flightpath table an error will be thrown.
     */
    public static Drone deliverOrder(ArrayList<Orders> todaysOrders, OrderDetails order, String deliverToLoc, Drone current_pos) throws SQLException {
        What3Words temp = new What3Words("localhost", webServerPort, deliverToLoc);
        Drone deliveryLocation = temp.getDestination();
        while (!current_pos.closeTo(deliveryLocation)) {
            if (!current_pos.isConfined()) {
                break;
            } else {
                Double distance = current_pos.distanceTo(deliveryLocation); // calculate the distance to the restaurant
                SortedMap<Double, Drone> validMoves = new TreeMap<>(); // sortedmaps to hold the best move as the first key
                SortedMap<Double, Integer> validAngles = new TreeMap<>();
                for (int angle = 0; angle <= 350; angle += 10) { // calculates every move possible at the current location
                    Drone moveTo = current_pos.nextPosition(angle);
                    // makes sure the move is confined and doesn't intersect the no-fly zones
                    if (moveTo.isConfined() && !checkForIntersection(current_pos, moveTo)) {
                        //will calculate the distance between the destination and the new position of the Drone
                        //The SortedMap validMoves will contain the key that will be the distance between the 2 locations and the value will be the new position of the Drone
                        //the SortedMap validAngles will contain the key that will be the distance between the 2 locations and the value will be the angle of that move.
                        validMoves.put(moveTo.distanceTo(deliveryLocation), moveTo);
                        validAngles.put(moveTo.distanceTo(deliveryLocation), angle);
                    }
                }
                if (validMoves.isEmpty()) { //if there are no valid moves.
                    // turn the current position into a junk value if there is valid move
                    current_pos = new Drone(-999.0, -999.0);
                    break;
                } else {
                    //create a junk value Drone that will be used later to hold the co-ordinates for the best move
                    Drone bestMove = new Drone(0.0, 0.0);

                    if (validMoves.get(validMoves.firstKey()).isConfined()) {
                        //validMoves.get(validMoves.firstkey()) will get the co-ordinates of the best move for the Drone to take.
                        bestMove = validMoves.get(validMoves.firstKey());
                    }
                    int bestAngle = validAngles.get(validAngles.firstKey());
                    //add the flightpath to the database
                    derby.addFlightPath(new Flightpath(order.getOrderNo(), current_pos.getLongitude(), current_pos.getLatitude(), bestAngle, bestMove.getLongitude(), bestMove.getLatitude()));
                    moveCount = moveCount - 1;
                    current_pos = bestMove;
                }
            }
        }
        if (current_pos.closeTo(deliveryLocation)) {
            current_pos = current_pos.nextPosition(-999);
            //adds the hover position for the Drone in the flighpath table in the database.
            derby.addFlightPath(new Flightpath(order.getOrderNo(), current_pos.getLongitude(), current_pos.getLatitude(), -999, current_pos.getLongitude(), current_pos.getLatitude()));
            moveCount = moveCount - 1;
            //get delivery cost
            for (Orders ord : todaysOrders) {
                if (order.getOrderNo().equals(ord.getOrderNo())) {
                    String[] str = order.getItem().split("@"); //split the string by the @ sign, the @ sign is what separates the unique items from each other
                    int deliveryCharge = menu.getDeliveryCost(str);
                    //add the successful delivery to the deliveries table in the database.
                    derby.addDeliveries(new Deliveries(ord.getOrderNo(), ord.getDeliverTo(), deliveryCharge));
                }
            }
        }
        return current_pos;
    }


    /**
     * This function will calculate the path from the Drones current position back to Appleton Tower, it will check
     * to make sure the Drone is within the confinement area. It will calculate all the possible moves from
     * where the Drone currently is and choose the move that shortens the distance between its current position and
     * Appleton Tower the most and is confined within the confinement area and does not intersect the no-fly zone.
     *
     * @param current_pos Drone object that contains the co-ordinates for the Drones current position.
     */

    public static void returnToAppleton(Drone current_pos) {
        Drone destination = APPLETON_TOWER; //set the destination
        while (!current_pos.closeTo(destination)) {
            if (!current_pos.isConfined()) { //checks to see if the current position of the drone is within the confinement area
                break;
            } else { // if the drone is within the confinement area
                if (moveCount == 0) { // if the Drone runs out of moves, a message will display to the user
                    System.out.println("ERROR: DRONE DID NOT REACH APPLETON TOWER");
                    break;
                } else { //if the drone still has moves left
                    SortedMap<Double, Drone> validMoves = new TreeMap<>(); // sortedmaps to hold the best move as the first key
                    SortedMap<Double, Integer> validAngles = new TreeMap<>();
                    for (int angle = 0; angle <= 350; angle += 10) { // calculates every move possible at the current location
                        Drone moveTo = current_pos.nextPosition(angle);
                        // makes sure the move is confined and doesn't intersect the no-fly zones
                        //checks whether the possible move is within the confinement area and doesn't intersect the no-fly zone
                        if (moveTo.isConfined() && !checkForIntersection(current_pos, moveTo)) {
                            validMoves.put(moveTo.distanceTo(destination), moveTo);
                            validAngles.put(moveTo.distanceTo(destination), angle);
                        }
                    }
                    if (validMoves.isEmpty()) { //if there are no possible moves a junk location will be returned
                        current_pos = new Drone(-999.0, -999.0);
                        break;
                    } else {
                        //validMoves.get(validMoves.firstkey()) will get the co-ordinates of the best move for the Drone to take.
                        Drone bestMove = validMoves.get(validMoves.firstKey());
                        moveCount = moveCount - 1;
                        current_pos = bestMove;
                    }
                    if (current_pos.closeTo(destination)) {
                        current_pos = current_pos.nextPosition(-999);
                        System.out.println("DRONE HAS ARRIVED BACK AT APPLETON TOWER");
                        moveCount = moveCount - 1;
                        break;
                    }
                }
            }
        }
        if (!current_pos.closeTo(destination)) {
            System.out.println("DRONE HAS RETURNED");
        }
        if (!current_pos.isConfined()) {
            System.out.println("NO ROUTE BACK TO APPLETON TOWER");
        }
    }

    /**
     * This function uses geometry to check if the line between point1 and point2 intersects with the line between
     * point3 and point4
     *
     * @param point1 Drone object, contains the co-ordinates for point1
     * @param point2 Drone object, contains the co-ordinates for point2
     * @param point3 Drone object, contains the co-ordinates for point3
     * @param point4 Drone object, contains the co-ordinates for point4
     * @return a Boolean, true if the lines intersect and false if they do not.
     */
    public static Boolean intersect(Drone point1, Drone point2, Drone point3, Drone point4) {
        Drone e = new Drone(point2.getLongitude() - point1.getLongitude(), point2.getLatitude() - point1.getLatitude());
        Drone f = new Drone(point4.getLongitude() - point3.getLongitude(), point4.getLatitude() - point3.getLatitude());
        Drone p = new Drone(-e.getLatitude(), e.getLongitude());
        double h = (((point1.getLongitude() - point3.getLongitude()) * p.getLongitude()) + ((point1.getLatitude() - point3.getLatitude()) * p.getLatitude())) / ((f.getLongitude() * p.getLongitude()) + (f.getLatitude() * p.getLatitude()));
        if (0 <= h && h <= 1) {
            return true;
        } else return false;
    }

    /**
     * This function takes 2 parameters, the current position of the Drone and the position the Drone wants to
     * travel to; the desired position. The function will check if the line between these two points intersects any
     * line contained within the no-fly zone.
     *
     * @param current_position Drone object containing the co-ordinates for the current position of the Drone.
     * @param desired_position Drone object containing the co-ordinates for the desired position of the Drone.
     * @return a Boolean, true if the Drone travels along a line that intersects any line in the no-fly zones and
     * false if it doesn't intersect any lines.
     */
    public static Boolean checkForIntersection(Drone current_position, Drone desired_position) {
        ArrayList<ArrayList<Drone>> doNotCrossZones = nofly.doNotCrossLines();
        Boolean binary = false;
        for (ArrayList<Drone> zone : doNotCrossZones) { // for each zone in the no-fly-zones
            for (int i = 0; i < zone.size(); i++) {
                if (i == zone.size() - 1) { //if at the last element of the zone list
                    //check if there is an intersection from the Drone move and the final co-ordinate and the first co-ordinate line in the zone.
                    if ((intersect(current_position, desired_position, zone.get(i), zone.get(0)))) {
                        binary = true;
                        break;
                    }
                } else {
                    //check if there is an intersect between the Drone move and the line between at the given co-ordinates in the no-fly zone.
                    if (intersect(current_position, desired_position, zone.get(i), zone.get(i + 1))) {
                        binary = true;
                        break;
                    }
                }
            }
        }
        return binary;
    }
}