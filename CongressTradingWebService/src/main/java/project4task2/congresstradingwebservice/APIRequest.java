package project4task2.congresstradingwebservice;

import org.bson.types.ObjectId;

public class APIRequest {

    private ObjectId id;
    private long processTime;

    public APIRequest() {

    }

    public APIRequest(ObjectId id, long processTime) {
        this.processTime = processTime;
        this.id = id;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(processTime);
    }
}
