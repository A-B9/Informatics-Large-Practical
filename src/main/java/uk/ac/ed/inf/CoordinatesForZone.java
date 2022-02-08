package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * This class represents a CoordinatesForZone object that holds the information parsed from the Json file for the No-Fly zone,
 * specifically it will be used to store the information about the Geometry of a No-Fly zone.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */

public class CoordinatesForZone {

    private final String type;
    private final ArrayList<ArrayList<Double[]>> coordinates;

    /**
     * Constructs and initialises the CoordinatesForZone object, it takes 2 parameters
     * one that corresponds to the type of the geometry and another that contains the
     * coordinates of the geometry
     * @param type is a String that is the type of geometry
     * @param coordinates is an ArrayList that contains an ArrayList of Double[], the Double[] holds
     *                    the longitude, latitude information for the points in the geometry.
     */
    CoordinatesForZone(String type, ArrayList<ArrayList<Double[]>> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    /**
     * This function will return the value of type for the specific CoordinatesForZone object
     * @return String value that contains the type value of the CoordinatesForZone object
     */
    public String getType() {return this.type;}

    /**
     * This function will return the information contained in the coordinates variable for the specific
     * CoordinatesForZone object.
     * @return the ArrayList that contains an ArrayList of Double[] contained in the CoordinatesForZone object.
     */
    public ArrayList<ArrayList<Double[]>> getCoordinates() {return this.coordinates;}

}
