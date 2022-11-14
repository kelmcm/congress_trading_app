/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 *
 * date: 2022.11.01
 * purpose: the purpose of this class is to provide an object for the JSON response from the QuiverQuant API
 * note: this is for both houses of congress
 */

package edu.heinz.ds.project4task2;

import com.google.gson.Gson;

public class CongressTrades {

    String ReportDate;	//  Date the transaction was reported
    String TransactionDate;	// Date the transaction took place
    String Ticker; // Ticker of shares transacted
    String Representative;	// Name of Congressperson who made the transaction
    String House;	// House of Congress that the representative belongs to
    String Transaction;	// Purchase or Sale
    float Amount;	// Lower bound of transaction size ($). Congressional trades are reported as a range of values, this variable is the lower bound of that range
    String Range;	// Full transaction size range

    public CongressTrades(String reportDate, String transactionDate, String ticker, String representative, String house, String transaction, float amount, String range) {
        ReportDate = reportDate;
        TransactionDate = transactionDate;
        Ticker = ticker;
        Representative = representative;
        House = house;
        Transaction = transaction;
        Amount = amount;
        Range = range;
    }

    /**
     * Override Java's toString method
     * @override overrides java lang's to string method
     * @return A JSON representation of all of this block's data is returned.
     */
    public String toString() {
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        String messageToSend = gson.toJson(this);
        // Display the JSON string
        return messageToSend;
    }

    public String getReportDate() {
        return ReportDate;
    }

    public void setReportDate(String reportDate) {
        ReportDate = reportDate;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public String getTicker() {
        return Ticker;
    }

    public void setTicker(String ticker) {
        Ticker = ticker;
    }

    public String getRepresentative() {
        return Representative;
    }

    public void setRepresentative(String representative) {
        Representative = representative;
    }

    public String getHouse() {
        return House;
    }

    public void setHouse(String house) {
        House = house;
    }

    public String getTransaction() {
        return Transaction;
    }

    public void setTransaction(String transaction) {
        Transaction = transaction;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getRange() {
        return Range;
    }

    public void setRange(String range) {
        Range = range;
    }


}
