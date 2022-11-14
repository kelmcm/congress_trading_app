/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

package project4task2.congresstradingwebservice;

import org.bson.types.ObjectId;

public class HerokuResponse {

    private ObjectId id;
    private int numberOfRecords;

    public HerokuResponse() {}

    public HerokuResponse(ObjectId id, int numberOfRecords) {
        this.id = id;
        this.numberOfRecords = numberOfRecords;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public String toString() {
        return String.valueOf(numberOfRecords);
    }

}
