import com.google.gson.Gson;

public class SenateTradingResponse {
    String Date;
    String Ticker;
    String Senator;
    String Transaction;
    float Amount;
    String Range;

    // basic constructor for the API response
    SenateTradingResponse(String Date, String Ticker, String Senator, String Transaction, float Amount, String Range){
        this.Date = Date;
        this.Ticker = Ticker;
        this.Senator = Senator;
        this.Transaction = Transaction;
        this.Amount = Amount;
        this.Range = Range;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTicker() {
        return Ticker;
    }

    public void setTicker(String ticker) {
        Ticker = ticker;
    }

    public String getSenator() {
        return Senator;
    }

    public void setSenator(String senator) {
        Senator = senator;
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
