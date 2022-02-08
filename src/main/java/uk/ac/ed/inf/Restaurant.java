package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Represents a Restaurant object to hold the information parsed from the Json files.
 * Contains an inner class FoodItem to represent each food item on the Restaurants menu
 * @author Arbnor Bregu s1832263
 * @date 14/10/2021
 * @version 1.0
 */
public class Restaurant {

    private final String name;
    private final String location;
    private final ArrayList<FoodItem> menu;

    /**
     * Constructs and initialises the Restaurant object which takes in parameters corresponding to the Restaurants name, location and menu.
     * The name and location variables are both String variables and menu is an ArrayList of FoodItem objects.
     * @param name is the name of the restaurant, must be a String
     * @param location is the location of the restaurant, must be a String
     * @param menu is the menu of the restaurant, must be an ArrayList of FoodItem objects
     */
    public Restaurant(String name, String location, ArrayList<FoodItem> menu) {
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    /**
     * Gets the name of the Restaurant
     * @return the name of the Restaurant
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the location of the Restaurant
     * @return the location of the Restaurant
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the menu of the Restaurant object.
     * @return the menu
     */
     ArrayList<FoodItem> getMenu() {
        return menu;
    }

    /**
     * Represents a food item on the menu of the Restaurant, it holds the food items name and cost of the food item.
     */
    public static class FoodItem{
        private String item;
        private int pence;

        /**
         * Constructs and initialises the FoodItem object which takes in parameters item and pence which represent the name of the object and the value of it in pence.
         * @param item is a String variable that contains the name of the food item
         * @param pence is an integer variable that contains the value of the food item
         */
        public FoodItem(String item, int pence) {
            this.item = item;
            this.pence = pence;
        }

        /**
         * Gets the value of the food item
         * @return pence
         */
        public int getPence() {
            return pence;
        }

        /**
         * Gets the name of the food item
         * @return item
         */
        public String getItem() {
            return item;
        }
    }
}
