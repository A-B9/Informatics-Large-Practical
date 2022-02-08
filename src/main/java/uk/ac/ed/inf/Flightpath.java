package uk.ac.ed.inf;

/**
 * This class is used to represent the information that is needed to insert into a row in the flightpath table in the Derby database.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class Flightpath {

    final String orderNo;
    final double fromLongitude;
    final double fromLatitude;
    final int angle;
    final double toLongitude;
    final double toLatitude;

    /**
     * Constructs and initialises a Flighpath objects that contains 6 parameters, these are the order number, the starting
     * longitude point, the starting latitude point, the angle of travel, the destination longitude and the destination
     * latitude
     * @param orderNo is a String that is the unique order number for the current order
     * @param fromLongitude contains the longitude of the starting position of the Drone as a double
     * @param fromLatitude contains the latitude of the starting position of the Drone as a double
     * @param angle contains the angle of travel of the Drone as an integer
     * @param toLongitude contains the longitude of the destination position of the Drone as a double
     * @param toLatitude contains the latitude of the destination position of the Drone as a double
     */
    Flightpath(String orderNo, double fromLongitude, double fromLatitude, int angle, double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
    }

    /**
     * Display the information within Flightpath as a String.
     * @return a String containing the information of the Flightpath object.
     */
    public String toString() {
        return "Flightpath: " + orderNo + ", " + fromLongitude + ", " + ", " + fromLatitude + ", " + angle + ", " + toLongitude + ", " + toLatitude;
    }
}
