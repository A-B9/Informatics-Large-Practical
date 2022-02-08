package uk.ac.ed.inf;

/**
 * Stores data that is retrieved from the orders table in the Derby database
 *
 * @author Arbnor Bregu s1832263
 * @date 02/12/2021
 * @version 1.0
 */
public class Orders {

    private final String orderNo;
    private final String deliveryDate;
    private final String customer;
    private final String deliverTo;

    /**
     * Constructs and initialises the Orders object which takes in 4 parameters, the
     * order number, the delivery date, the name of the customer; in this case it is the
     * matriculation number of the students, and it also stores the location the order needs
     * to be delivered to.
     * @param orderNo is a String that contains the order number
     * @param deliveryDate is a String that contains the date of the delivery
     * @param customer is a String that contains the student matriculation number
     * @param deliverTo is a String that contains the WhatThreeWords address for the location of the delivery
     */
    Orders(String orderNo, String deliveryDate, String customer, String deliverTo) {
        this.orderNo = orderNo;
        this.deliveryDate = deliveryDate;
        this.customer = customer;
        this.deliverTo = deliverTo;
    }

    /**
     * Overrides the standard toString() method that will return the parameters
     * in an easy-to-read string for the user
     * @return a String that contains all the Orders object parameters in the correct order.
     */
    @Override
    public String toString() {
        return "("+this.orderNo+", " + this.deliveryDate + ", " + this.customer + ", " + deliverTo + ")";
    }

    /**
     * Gets the delviveryDate parameter
     * @return the deliveryDate parameter of the Orders object
     */
    public String getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Gets the getOrderNo parameter
     * @return the orderNo parameter of the Orders object
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * Gets the deliverTo parameter
     * @return the deliverTo parameter of the Orders object
     */
    public String getDeliverTo() {
        return deliverTo;
    }

    /**
     * Gets the customer paramter
     * @return the customer parameter of the Orders object
     */
    public String getCustomer() {
        return customer;
    }
}
