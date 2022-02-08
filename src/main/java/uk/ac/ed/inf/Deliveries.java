package uk.ac.ed.inf;

/**
 * This class is used to represent information that is needed to insert into a row in the deliveries table in the Derby
 * database.
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class Deliveries {

    protected final String orderNo, deliveredTo;
    protected final int costInPence;

    /**
     * Constructs and initialises the Deliveries object, which takes 3 parameters, the order number of the delivery,
     * the location the order was delivered to and the cost of the delivery; this includes the cost of the food and the
     * cost of delivery.
     * @param orderNo is a String that stores the unique order number for the current order
     * @param deliveredTo is a String that stores the WhatThreeWords address for the delivery location of the current order
     * @param costInPence is an integer that stores the cost of the delivery. This is the sum of the cost of the food and cost of delivery.
     */
    Deliveries(String orderNo, String deliveredTo, int costInPence) {
        this.costInPence = costInPence;
        this.deliveredTo = deliveredTo;
        this.orderNo = orderNo;
    }

    /**
     * Will turn the object into a String to be displayed.
     * @return a String containing the values of the Deliveries object.
     */
    public String toString() {
        return "Delivery: " + orderNo + ", " + deliveredTo + ", " + costInPence;
    }

    /**
     * This will retrieve the value of costInPence which is the delivery cost of the order.
     * @return costInPence
     */
    public int getCostInPence() {
        return costInPence;
    }
}
