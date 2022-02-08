package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * This class stores all the necessary information for a zone the Drone cannot fly through. This class will be used to store
 * the information for a zone when it is parsed from the .geojson file.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class NoFlyZone {

    private final String name, fill, type;
    private final ArrayList<ArrayList<Double[]>> coordinates;

    /**
     * Constructs and initialises a NoFlyZone object that holds 4 parameters. It will store the
     * name of the zone, the colour of the zone will be represented by, the type
     * of geometry the zone is and the coordinates of the zone.
     *
     * @param name is a String that stores the name of the zone.
     * @param fill is a String that stores the colour of the zone.
     * @param type is a String that stores the type of geometry the zone is.
     * @param coordinates is an ArrayList that contains an ArrayList of Double[].
     *                    The Double[] stores the information of a single
     *                    longitude, latitude point.
     */
    NoFlyZone(String name, String fill, String type, ArrayList<ArrayList<Double[]>> coordinates) {
        this.coordinates = coordinates;
        this.name = name;
        this.type = type;
        this.fill = fill;
    }


    /**
     * This will retrieve co-ordinates of the NoFlyZone
     *
     * @return the value of coordinates
     */
    public ArrayList<ArrayList<Double[]>> getCoordinates() {
        return coordinates;
    }

    /**
     * This will retrieve the type of the NoFlyZone
     *
     * @return return the value of type
     */
    public String getType() {
        return type;
    }

    /**
     * This will retrieve the name of the NoFlyZone
     *
     * @return the value of name.
     */
    public String getName() {return name;}

    /**
     * This will retrieve the fill value of the NoFlyZone
     *
     * @return the value of fill.
     */
    public String getFill() {return fill;}

}
