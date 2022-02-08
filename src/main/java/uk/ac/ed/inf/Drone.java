package uk.ac.ed.inf;
import java.lang.Math;
/**
 * The class Drone is used to manipulate the (longitude, latitude) location of the drone, it considers the Earth as a plane
 * and not as a sphere, it uses Euclidean distance to traverse around the Earth. The parameters latitude and longitude are public,
 * they can be seen and manipulated outside the class Drone.
 * @author Arbnor Bregu s1832263
 * @date 14/10/2021
 * @version 1.0
 */
public class Drone {

    public Double latitude;
    public Double longitude;
    static double moveDist = 0.00015;


    /**
     * Constructs and initialises an object called Drone at position (longitude, latitude), both parameters are doubles.
     * @param longitude longitude co-ordinate for the location of the object
     * @param latitude latitude co-ordinate for the location of the object
     */

    public Drone(Double longitude, Double latitude) {
        this.longitude = longitude; // −3.184319 and −3.192473
        this.latitude = latitude; // 55.942617 and 55.946233
    }

    /**
     * A getter that will retrieve the value of the Longitude
     * @return the Longitude value
     */

    public Double getLongitude() {
        return longitude;
    }

    /**
     * A getter that will retrieve the value of the Latitude
     * @return the latitude value
     */

    public Double getLatitude() {
        return latitude;
    }

    /**
     * distanceTo calculates the euclidean distance between the given point and pointA
     * @param pointA this is a Drone object with parameters (longitude, latitude)
     * @return the euclidean distance between the 2 points.
     */

    public Double distanceTo(Drone pointA) { // takes 2 points and finds the euclidean distance between them.
        Double pointA_long = pointA.getLongitude(); // get the longitude of pointA
        Double pointA_lat = pointA.getLatitude(); // get the latitude of pointA

        return Math.sqrt(Math.pow((this.longitude - pointA_long), 2) + Math.pow((this.latitude - pointA_lat),2));
    }

    /**
     * closeTo is a function that will see if the Drone object is close to pointA.
     * Drone is considered close to pointA if the euclidean distance between the 2 points is less than 0.00015
     * @param pointA is a Drone object with parameters (longitude, latitude)
     * @return true if the distance between the points is less than or equal to 0.00015, returns false otherwise
     */

    public Boolean closeTo(Drone pointA) {
        Drone temp = new Drone(this.longitude, this.latitude);
        Double dist = temp.distanceTo(pointA); // calculates the distance between the points
        // the distance tolerance is: 0.00015 degrees.
        return dist <= moveDist;
    }

    /**
     * nextPosition will rotate the position of object Drone depending on the given angle.
     * The angle is a rotation in degrees, where East is 0, North is 90, West is 180 and South is 270.
     * The value of angle cannot exceed 350 and must be a multiple of 10.
     * To show when the object is hovering the junk value '-999' for angle is used,
     * to show that it does not have an effect on the longitude and latitude of the object.
     *
     * @param angle is the angle that the object will be rotated by. Must be a multiple of 10 and 0 <= angle <=350.
     * @return the new point with changed longitude and latitude  if it has been rotated, otherwise returns the original position
     */

    public Drone nextPosition(int angle) {
        int mod_angle = angle % 360; // in the case that angle exceeds 360.
        double rad = mod_angle * (Math.PI /180); // convert the angle from degrees to radians

        if (mod_angle % 10 == 0) { // will make sure the value of mod_angle is a multiple of 10

            // calculate the new longitude and latitude for the rotated object
            double move_lat = moveDist * Math.sin(rad);
            double move_long = moveDist * Math.cos(rad);
            double new_long = this.longitude + move_long;
            double new_lat = this.latitude + move_lat;

            return new Drone(new_long, new_lat);
        }
        else if (angle == -999) { // used to indicate when the object is hovering using junk value -999
            // returns the original longitude and latitude of the point
            return new Drone(this.longitude, this.latitude);
        }
        else {
            // In the case that the value of angle does not fit the specified criteria
            System.out.println("Error: Not an angle that is accepted");
            return new Drone(this.longitude, this.latitude);
        }
    }

    /**
     * Checks whether the Drone object is within the "Drone Confinement Area", the object is considered within this area
     * if and only if the current position of the object is strictly less than the boundary limits specified by the
     * confinement area.
     *
     * @return true if the object is within "The Drone Confinement Area", false otherwise.
     */

    public Boolean isConfined() {
        //if statement to check if the co-ordinates are within "The Drone Confinement Area" boundaries
        if(55.942617 < latitude && latitude < 55.946233) {
            return -3.192473 < longitude && longitude < -3.184319;
        }
        else return false; // return false if the longitudes and latitudes are not within these boundaries.
    }


    /**
     * Display the information within Drone as a string.
     * @return a String containing the information of the Drone object.
     */
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }


}