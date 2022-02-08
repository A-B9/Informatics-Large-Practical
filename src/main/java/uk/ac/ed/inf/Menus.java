package uk.ac.ed.inf;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to access the web server to get the content of the menus.json file and deserialize the json
 * strings into a Restaurant object.
 *
 * @author Arbnor Bregu s1832263
 * @date 14/10/2021
 * @version 1.0
 */
public class Menus {

    private String name;
    private String port;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static ArrayList<Restaurant> restaurantList;


    /**
     * Constructs and initialises an object called Menus, 'name' and 'port' are both Strings. It allows access to the
     * website required to access the menus.json file, it also parses the json file information and saves it in restaurantList
     *
     * @param name is the name of the website
     * @param port is the port we need to access for the given website
     * @throws IOException if there are issues with the input and/or output
     * @throws ConnectException if the web server is not running and therefore client.send() will not work.
     * @throws IllegalArgumentException if the parameters passed into the constructor are not appropriate, i.e. if they are not typed correctly.
     */
    public Menus(String name, String port) {
        this.name = name;
        this.port = port;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + name + ":"+ port + "/menus/menus.json")).build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            // checks whether Menus can successfully access the webserver
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<ArrayList<Restaurant>>(){}.getType();
                // convert the Json string into an ArrayList of Restaurant objects and save it into restaurantList
                restaurantList = new Gson().fromJson(response.body(), listType);
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
     * This function will convert the arraylist that contains the menu data in the json file into a hashmap.
     * It will take the string information of item as the Key and the corresponding value of pence of the item,
     * as the Value pair of the Key.
     *
     * @param arrayList holds the json file information
     * @return will return the hashmap with the json file information converted into a hashmap
     */
    private static HashMap<String, Integer> convertArrayListToHashMap(ArrayList<Restaurant.FoodItem> arrayList) {
        HashMap<String, Integer> hashMap = new HashMap<>(); //creates a new, empty HashMap
        //this loop will iterate through the array of FoodItem objects and get their name (item) and cost (pence)
        //it will then assign item as the key and pence as the value
        for (Restaurant.FoodItem str : arrayList) { //
            hashMap.put(str.getItem(), str.getPence());
        }
        return hashMap;
    }

    /**
     * This function takes a variable number of string inputs. It will access each Restaurant's menu and check whether
     * the food item is contained in the Restaurants menu, if it is in the menu it will add the price of the food onto
     * deliveryCost which tracks the total cost of the delivery. If it is not in the present menu it will check the next
     * menu, if there are no more Restaurant menus left it will return an error message
     *
     * @param food is the food item(s) that have been ordered (these are the inputs of the method).
     * @return the sum of the individual food items, plus a 50 pence charge that is the delivery cost
     */

    public int getDeliveryCost(String... food) {
        int deliveryCost = 0; // will track the cost of the delivery
        int failedQueries = 0; //track how many restaurants do not have the food item, if any
        ArrayList<Restaurant> restaurants = restaurantList;
        //iterate through the input strings
        for (String foodItem : food) {
            //loop through all the restaurants
            for (int restaurantMenu = 0; restaurantMenu < restaurants.size(); restaurantMenu++) {

                //convert the menu ArrayList of the current restaurant and checks if the item is in the menu

                HashMap<String, Integer> menus = convertArrayListToHashMap(restaurants.get(restaurantMenu).getMenu());
                if (menus.containsKey(foodItem)) {
                    deliveryCost += menus.get(foodItem); //adds the cost of the item onto the delivery cost
                }
                else if (!menus.containsKey(foodItem)) {
                    failedQueries += 1;
                }
            }
            // if no restaurant has the food item, it will output an error message.
            if (failedQueries == restaurants.size()) {
                System.out.println("Error: Food item: " + foodItem + ", not available at any available Restaurant");
            }
        }
        // if some food items can be delivered, it will add on the delivery cost
        if (deliveryCost != 0) {
            deliveryCost += 50;
        }
        return deliveryCost;
    }

    /**
     * This function takes a String input containing the name of a food item, this function will check through each of
     * the menus of all possible Restaurants until it finds the Restaurant the food item is found in. If there is a match
     * then the function will return the What3Words address location of the corresponding restaurant. If there is no match an
     * error message will be printed telling the user the food item is not found in any available Restaurant.
     *
     * @param food String, contains the name of the food item.
     * @return String, the What3Words location for the Restaurant the food item is located in.
     */
    public String getLocation(String food) {
        ArrayList<Restaurant> restaurants = restaurantList;
        int failedQueries = 0;
        String located = "";
            //loop through all the restaurants
            for (int restaurantMenu = 0; restaurantMenu < restaurants.size(); restaurantMenu++) {

                //convert the menu ArrayList of the current restaurant and checks if the item is in the menu
                HashMap<String, Integer> menus = convertArrayListToHashMap(restaurants.get(restaurantMenu).getMenu());
                if (menus.containsKey(food)) {
                    located = restaurants.get(restaurantMenu).getLocation();//adds the cost of the item onto the delivery cost

                }
                else if (!menus.containsKey(food)) {
                    failedQueries += 1;
                }
            }
            // if no restaurant has the food item, it will output an error message.
            if (failedQueries == restaurants.size()) {
                System.out.println("Error: Food item: " + food + ", not available at any available Restaurant");
            }
        return located;
    }
}
