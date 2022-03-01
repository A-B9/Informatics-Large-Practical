package uk.ac.ed.inf;
import java.sql.*;
import java.util.ArrayList;

/**
 * This class will access the Derby database and retrieve information from the
 * orders and orderDetails table in the database, it will also be used to create the flightpath
 * and deliveries table that will be added to the database. This class will also
 * update these tables with the relevant information when needed.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class Derby {

    private final String port;
    private final String derby;
    private final Connection conn;
    private Statement statement; // hello world



    /**
     * Constructs and initialises the Derby object that takes in 2 parameters, derby
     * and port, it gives access to the database. The flightpath and deliveries tables will be created
     * and the orders and orderDetails tables can be accessed using this constructor.
     * @param derby is the name of the database
     * @param port is the port that we need to access for the database
     * @throws SQLException if something goes wrong with creating, dropping or searching a table a SQLException is thrown.
     */
    public Derby(String derby, String port) throws SQLException {
        this.derby = derby;
        this.port = port;

        conn = DriverManager.getConnection("jdbc:" + derby + "://localhost:"+ port + "/" + derby + "DB");
        //Create a statement object that we can use for running various
        //SQL statement commands against the database.
        statement = conn.createStatement();

        DatabaseMetaData databaseMetaData = conn.getMetaData();
        //Note: must capitalise ORDERS in the call to getTables
        ResultSet resultSetFlighpath = databaseMetaData.getTables(null, null, "FLIGHTPATH", null);
        //If the resultASet is not empty then the tables exists,  so we can drop it

        if (resultSetFlighpath.next()) {
            statement.execute("drop table flightpath");

            statement.execute("create table flightpath(" +
                    "orderNo char(8), " + //8 character order num for the lunch order which the drone is currently working on
                    "fromLongitude double, " + // the long of the drone at the start of the move
                    "fromLatitude double, " + // the lat of the drone at the start of the move
                    "angle int, " + //angle of travel of the drone for this move
                    "toLongitude double, " + // long of drone at the end of the move
                    "toLatitude double)"); //lat of drone at the end of the move
        }
        else {
            statement.execute("create table flightpath(" +
                    "orderNo char(8), " + //8 character order num for the lunch order which the drone is currently working on
                    "fromLongitude double, " + // the long of the drone at the start of the move
                    "fromLatitude double, " + // the lat of the drone at the start of the move
                    "angle int, " + //angle of travel of the drone for this move
                    "toLongitude double, " + // long of drone at the end of the move
                    "toLatitude double)"); //lat of drone at the end of the move
        }
        ResultSet resultSetDeliveries = databaseMetaData.getTables(null, null, "DELIVERIES", null);

        if (resultSetDeliveries.next()) {
            statement.execute("drop table deliveries");

            statement.execute("create table deliveries(" +
                    "orderNo char(8), " + // hexadecimal string assigned to this order in the orders table.
                    "deliveredTo varchar(19), " + // the WhatThreeWords address of delivery location
                    "costInPence int)"); //total cost of order including delivery charge
        }
        else {
            statement.execute("create table deliveries(" +
                    "orderNo char(8), " + // hexadecimal string assigned to this order in the orders table.
                    "deliveredTo varchar(19), " + // the WhatThreeWords address of delivery location
                    "costInPence int)"); //total cost of order including delivery charge
        }

                //We use a PreparedStatement Object which is form of statement
                //which allows us to execute parameterised query
    }

    /**
     * This method takes a single parameter that is a Flightpath object, it inserts the parameters
     * of the Flightpath object into the flightpath table.
     * @param flightpath is a Flightpath object that will be inserted into the flightpath table.
     * @throws SQLException if an error occurs with inserting the required parameters into the table an SQLException will be thrown.
     */
    public void addFlightPath(Flightpath flightpath) throws SQLException {
        PreparedStatement psFlight = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
        psFlight.setString(1, flightpath.orderNo);
        psFlight.setString(2, String.valueOf(flightpath.fromLongitude));
        psFlight.setString(3, String.valueOf(flightpath.fromLatitude));
        psFlight.setString(4, String.valueOf(flightpath.angle));
        psFlight.setString(5, String.valueOf(flightpath.toLongitude));
        psFlight.setString(6, String.valueOf(flightpath.toLatitude));

        psFlight.execute();
        //the parameters are numbered so we have to fill them in with relevant data.

    }

    /**
     * This method takes no parameters, it will read the data stored in the flightpath table and will save the parameters
     * of each row as a single Flightpath object. It will then add each of these Flightpath objects into an ArrayList.
     * @return an ArrayList of Flightpath objects.
     * @throws SQLException if an error occurs when pulling data from the flightpath table an SQLException will be thrown
     */
    public ArrayList<Flightpath> readFlightpathData() throws SQLException {
        final String flightQuery = "select * from flightpath";
        PreparedStatement psFlightQuery = conn.prepareStatement(flightQuery);

        ArrayList<Flightpath> flightList = new ArrayList<>();
        ResultSet rs = psFlightQuery.executeQuery();
        while (rs.next()) {
            // parse and store the values of each column in the flightpath table, stores each row as a single Flighpath object
            String orderNo = rs.getString("orderNo");
            double fromLongitude = rs.getDouble("fromLongitude");
            double fromLatitude = rs.getDouble("fromLatitude");
            int angle = rs.getInt("angle");
            double toLongtiude = rs.getDouble("toLongitude");
            double toLatitude = rs.getDouble("toLatitude");
            Flightpath flight = new Flightpath(orderNo, fromLongitude , fromLatitude, angle, toLongtiude, toLatitude);
            flightList.add(flight); //add the Flighpath object to the ArrayList of Flighpath objects.
        }
        return flightList; //return the ArrayList object
    }



    /**
     *This method takes a single parameter that is a Deliveries object and will insert the parameters
     * of the Deliveries object into the deliveries table.
     * @param delivery is a Deliveries object that will be inserted into the deliveries table.
     * @throws SQLException if an error occurs with inserting the required parameters into the table an SQLException will be thrown.
     */
    public void addDeliveries(Deliveries delivery) throws SQLException {
        PreparedStatement psDelivery = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
        psDelivery.setString(1, delivery.orderNo);
        psDelivery.setString(2, String.valueOf(delivery.deliveredTo));
        psDelivery.setString(3, String.valueOf(delivery.costInPence));

        psDelivery.execute();
        //the parameters are numbered so we have to fill them in with relevant data.
    }

    /**
     * This method takes no parameters, it reads the data stored in the deliveries table and will save the parameters
     * of each row as a single Deliveries object. It will add each of these Deliveries objects into an ArrayList.
     * @return an ArrayList of Deliveries objects.
     * @throws SQLException if an error occurs when pulling data from the deliveries table an SQLException will be thrown.
     */
    public ArrayList<Deliveries> readDeliveriesData() throws SQLException{
        final String deliveryQuery = "select * from deliveries";
        PreparedStatement psDeliveryQuery = conn.prepareStatement(deliveryQuery);

        ArrayList<Deliveries> deliveryList = new ArrayList<>();
        ResultSet rs = psDeliveryQuery.executeQuery();
        while (rs.next()) {
            // parse and store the values of each column in the deliveries table, stores each row as a single Deliveries object
            String orderNo = rs.getString("orderNo");
            String deliveredTo = rs.getString("deliveredTo");
            Integer costInPence = rs.getInt("costInPence");
            Deliveries delivery = new Deliveries(orderNo, deliveredTo, costInPence);
            deliveryList.add(delivery); //add the Deliveries object into the ArrayList of Deliveries objects
        }
        return deliveryList; // return the ArrayList
    }

    /**
     *This method will read the data contained in the orders table and will save the parameters of each row as a single
     * Orders object. It will then add all of these Orders objects into an ArrayList.
     * @return an ArrayList of Orders objects.
     * @throws SQLException if an error occurs when trying to pull data from the orders table an SQLException will be thrown.
     */

    public ArrayList<Orders> readOrderData() throws SQLException {
        final String orderQuery = "select * from orders";
        PreparedStatement psOrderQuery = conn.prepareStatement(orderQuery);
        ArrayList<Orders> orderList = new ArrayList<>();
        ResultSet rs = psOrderQuery.executeQuery();
        while (rs.next()) {
            String orderNo = rs.getString("orderNo");
            String date = rs.getString("deliveryDate");
            String customer = rs.getString("customer");
            String deliverto = rs.getString("deliverTo");
            Orders order = new Orders(orderNo, date , customer, deliverto);
            orderList.add(order);
        }
        return orderList;
    }

    /**
     * This method will read the data contained in the orderDetails table and will save the parameters of each row
     * as a single OrderDetails object. It will then add all of these OrderDetails objects into an ArrayList.
     * @return an ArrayList of OrderDetails objects
     * @throws SQLException if an error occurs when trying to pull data from the orderDetails table an SQLException will be thrown.
     */
    public ArrayList<OrderDetails> readOrderDetailsData() throws SQLException {
        final String tableQuery = "select *  from orderDetails";
        PreparedStatement psTableQuery = conn.prepareStatement(tableQuery);
        ArrayList<OrderDetails> orderDetails = new ArrayList<>();
        ResultSet rs = psTableQuery.executeQuery();
        while(rs.next()) {
            String orderNo = rs.getString("orderNo");
            String item = rs.getString("item");
            OrderDetails orderDetail = new OrderDetails(orderNo, item);
            orderDetails.add(orderDetail);
        }
        return orderDetails;
    }

}