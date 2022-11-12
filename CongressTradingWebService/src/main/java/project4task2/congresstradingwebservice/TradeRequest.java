package project4task2.congresstradingwebservice;

import org.bson.types.ObjectId;
import java.util.Date;

public class TradeRequest {

    private ObjectId id;
    private String mobileDevice;
    private String requestMethod;
    private String ticker;
    private Date requestedAt;

    public TradeRequest() {}

    public TradeRequest(ObjectId id, String mobileDevice, String requestMethod, String ticker, Date requestedAt) {
        this.id = id;
        this.mobileDevice = mobileDevice;
        this.requestMethod = requestMethod;
        this.ticker = ticker;
        this.requestedAt = requestedAt;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
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

}
