/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

package project4task2.congresstradingwebservice;

import org.bson.types.ObjectId;
import java.util.Date;

public class Log {

    private ObjectId id;
    private String mobileDevice;
    private String language;
    private String ticker;
    private Date requestedAt;
    private int numberOfRecords;
    private long processTime;

    public Log() {}

    public Log(ObjectId id, String mobileDevice, String language, String ticker, Date requestedAt, int numberOfRecords, long processTime) {
        this.id = id;
        this.mobileDevice = mobileDevice;
        this.language = language;
        this.ticker = ticker;
        this.requestedAt = requestedAt;
        this.numberOfRecords = numberOfRecords;
        this.processTime = processTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMobileDevice() {
        return mobileDevice;
    }

    public void setMobileDevice(String mobileDevice) {
        this.mobileDevice = mobileDevice;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    @Override
    public String toString() {
        return ticker;
    }

}
