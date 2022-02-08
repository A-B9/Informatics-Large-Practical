package uk.ac.ed.inf;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.mapbox.geojson.*;

/**
 * This class will be used to retrieve and store every No-Fly zone, these are Zones that the Drone
 * cannot fly through. This class will access the webserver and will access the correct file to
 * retrieve the json file that contains the information for these zones.
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */

public class NoFly {

    private String name;
    private String port;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static FeatureCollection fc;


    /**
     * Constructs and initialises a NoFly object, it takes 2 parameters. The parameters will be used
     * to access the correct website to retrieve the no-fly-zone.geojson file which will be
     * parsed to retrieve all the required information about the zones.
     *
     * @param name is the name of the website
     * @param port is the port that we need to access for the given website
     */
    public NoFly(String name, String port) {
        this.name = name;
        this.port = port;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" +name+ ":"+port+"/buildings/no-fly-zones.geojson")).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            //checks whether NoFly can successfully access the webserver
            if (response.statusCode() == 200) {
                // save the information of the feature collection into fc.
                fc = FeatureCollection.fromJson(response.body());
            }
            //checks whether the webserver cannot be found
            else if (response.statusCode() == 404) {
                System.out.println("Error: Website not found");
                System.exit(1);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("Illegal Argument Exception: inappropriate argument passed into the method");
        } catch (ConnectException e) {
            System.out.println("Fatal error: Unable to connect to " + name + " at port " + port + ".");
            System.exit(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will store each of the restricted zones as a NoFlyZone object and then store those objects into an ArrayList.
     * It will serialise the Json file containing the no-fly zone information and then retrieve the name of the zone, the type of the
     * geometry, the colour of the fill and the co-ordinates of the zone. It will store all the zones in an ArrayList.
     *
     * @return an ArrayList of all the zones the Drone cannot fly into.
     */
    public ArrayList<NoFlyZone> getZones() {
        ArrayList<NoFlyZone> zones = new ArrayList<>();
        for (Feature f : fc.features()) {

            String name = f.properties().get("name").toString().replace("\"", ""); // remove the quotations from the string and store the string
            String fill = f.properties().get("fill").toString().replace("\"", "");

            String jsonStringGeometry = f.geometry().toJson(); // turn the geometry for each zone into a json string
            CoordinatesForZone place = new Gson().fromJson(jsonStringGeometry, CoordinatesForZone.class); //store the json geometry as a CoordinatesForZone object

            String type = place.getType(); //get the type of the geometry from the CoordinatesForZone
            ArrayList<ArrayList<Double[]>> coords = place.getCoordinates(); //get the co-ordinates for the geometry

            NoFlyZone loc = new NoFlyZone(name, fill, type, coords); // turn the zone into a NoFlyZone object with the correct details.
            zones.add(loc); //add the object to the ArrayList of zones
        }
        return zones;
    }

    /**
     * This function will iterate through all the co-ordinates for each zone in the no-fly zone and save them as a Drone
     * object. Each zones co-ordinates will be saved as a separate ArrayList of Drone objects and all of these ArrayLists
     * will be stored in another ArrayList.
     *
     * @return An ArrayList that contains ArrayLists of Drone objects that store the information of each co-ordinate in the no-fly zone.
     */
    public ArrayList<ArrayList<Drone>> doNotCrossLines() {

        ArrayList<ArrayList<Drone>> dontCrossZone = new ArrayList<>(); //create an ArrayList to hold the ArrayLists that will contain the coordinates of all the no-fly zones
        for (NoFlyZone zone : getZones()) { //for each individual zone
            ArrayList<Drone> dontCrossLines = new ArrayList<>(); //create an ArrayList to hold the co-ordinates of the zone
            for (ArrayList<Double[]> points: zone.getCoordinates()) {
                for (Double[] point : points) {
                    dontCrossLines.add(new Drone(point[0], point[1])); //turn the Double[] that stores the co-ordinates into a Drone object and add it to the ArrayList
                }
            }
            dontCrossZone.add(dontCrossLines);
        }
        return dontCrossZone;
    }
}