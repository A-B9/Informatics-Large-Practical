package uk.ac.ed.inf;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *  This class is used to retrieve the information from the details.json file linked to the corresponding What3Words address
 *  that is used to access it on the webserver.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class What3Words {
    private String name;
    private String port;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static W3WFile what3words;


    /**
     * Constructs a What3Words object, "name", "port", "w3w" are Strings. This allows access to the
     * website that will contain a file corresponding to the What3Words location, the 3 word combination for
     * the What3Words location is stored in the w3w parameter.
     * @param name String, is the name of the website
     * @param port String, is the port we need to access for the given website
     * @param w3w String, is the What3Words location, this will allow access to the relevant .json file
     *            that we need to find the Longitude, Latitude version of the What3Words location.
     */
    public What3Words(String name, String port, String w3w) {
        String betterw3w = w3w.replace(".", "/");

        this.name = name;
        this.port = port;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" +name+ ":"+port+"/words/" + betterw3w + "/details.json")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // checks if the function can successfully access the webserver
            if (response.statusCode() == 200) {
                what3words = new Gson().fromJson(response.body(), W3WFile.class);
            }
            //checks if the webserver cannot be found
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
     * getDestination will retrieve the exact coordinates for the what3words object.
     * @return Drone, will turn the coordinates for the location into a Drone object
     */
    public Drone getDestination() {
        W3WFile what3wordsDetails = what3words;
        return what3wordsDetails.getCoordinates().coordinatesToLongLat();
    }
}
