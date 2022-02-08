package uk.ac.ed.inf;

/**
 * Stores data that is retrieved from the orderDetails table in the Derby database.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */

public class OrderDetails {

    private final String orderNo;
    private final String item;

    /**
     * Constructs and initialises the OrderDetails objects which takes in parameters
     * corresponding to the order number and the name of the item that was ordered.
     *
     * @param orderNo is a String variable that contains the unique order number
     * @param item is a String variable that contains the name of the item that was ordered.
     */
    OrderDetails(String orderNo, String item) {
        this.item = item;
        this.orderNo = orderNo;
    }

    /**
     * Overrides the standard toString() method, this will return the order number and
     * name of the item as a string which is easier to read by a user.
     * @return a String that contains the order number and name of the item.
     */
    @Override
    public String toString() {
        return "(" + this.orderNo + ", " + this.item + ")";
    }

    /**
     * Gets the orderNo parameter of a OrderDetails object
     * @return the orderNo of the OrderDetails object
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * Gets the item parameter of a OrderDetails object
     * @return the item of the OrderDetails object
     */
    public String getItem() {
        return item;
    }
}
