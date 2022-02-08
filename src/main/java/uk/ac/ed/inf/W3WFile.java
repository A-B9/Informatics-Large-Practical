package uk.ac.ed.inf;

/**
 * This class is used to store the parsed information from the What3Words files,
 * What3Words has been abbreviated into W3WFile for efficiency.
 * Contains 4 inner classes; InsideSquare, SouthWest, NorthEast, Coordinates.
 * @author Arbnor Bregu s1832263
 * @date 30/10/2021
 * @version 1.0
 */
public class W3WFile {

    private final String country;
    private final InsideSquare square;
    private final String nearestPlace;
    private final Coordinates coordinates;
    private final String words;
    private final String language;
    private final String map;

    /**
     * Constructs the W3WFile object, this object takes in 6 parameters corresponding to the Country of the location,
     * the square the location can be found, nearestPlace contains the name of the closest city,
     * the coordinates for the location, the 3 words specific to the location, the language of the area
     * and the url for the location on a map.
     * @param country String that contains the name of the Country.
     * @param square a InsideSquare object, that contains information about the square.
     * @param nearestPlace String that contains the name of the nearest city.
     * @param coordinates a Coordinates object that holds the longitude and latitude of the location.
     * @param words a String that contains the unique 3 word combination for the location, from What3Words.
     * @param language String that contains the main spoken language of the area.
     * @param map String that contains a link to another website.
     */
    public W3WFile(String country, InsideSquare square, String nearestPlace,
                   Coordinates coordinates, String words, String language, String map) {

        this.country = country;
        this.square = square;
        this.nearestPlace = nearestPlace;
        this.coordinates = coordinates;
        this.words = words;
        this.language = language;
        this.map = map;
    }

    /**
     * This function will retrieve the coordinates of the W3WFile object
     * @return the Coordinates variable for the W3WFile object.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Represents the square for the location
     */
    public static class InsideSquare {
        SouthWest sw;
        NorthEast ne;

        /**
         * Constructs a InsideSquare object that takes a SouthWest and a NorthEast object,
         * these hold the south-west point and north-east point of the object respectively.
         * @param sw a SouthWest object that contains the location of the southwest point
         *           of the square.
         * @param ne a NorthEast object that contains the location of the northeast point
         *           of the square.
         */
        public InsideSquare(SouthWest sw, NorthEast ne) {
            this.sw = sw;
            this.ne = ne;
        }

        /**
         * Represents the Southwest coordinates for the square
         */
        public static class SouthWest {
            private Double lng;
            private Double lat;

            /**
             * Constructs a SouthWest object that contains the longitude, latitude
             * location for the SouthWest point of the square.
             * @param lng Double, the longitude of the point.
             * @param lat Double, the latitude of th point.
             */
            public SouthWest(Double lng, Double lat) {
                this.lng = lng;
                this.lat = lat;
            }
        }

        /**
         * Represents the NorthEast coordinates for the square
         */
        public static class NorthEast {
            private Double lng;
            private Double lat;

            /**
             * Constructs a NorthEast object that contains the longitude, latitude
             * location for NorthEast point of the square.
             * @param lng Double, the longitude of the point
             * @param lat Double, the latitude of the point
             */
            public NorthEast(Double lng, Double lat) {
                this.lng = lng;
                this.lat = lat;
            }
        }
    }

    /**
     * Represents the exact coordinates for the What3Words location.
     */
    public static class Coordinates {
        Double lng;
        Double lat;

        /**
         * Constructs a Coordinates object that contains the exact longitude, latitude
         * location for the What3Words location.
         * @param lng Double, the longitude of the point
         * @param lat Double, the latitude of the point
         */
        public Coordinates(Double lng, Double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        /**
         * This method will get the coordinates of the location and turn it into a Drone object
         * @return a Drone object that represents the location of the Coordinates object.
         */
        public Drone coordinatesToLongLat() {
            return new Drone(lng, lat);
        }
    }

}
