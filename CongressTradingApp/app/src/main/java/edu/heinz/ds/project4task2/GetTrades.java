/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 *
 * date: 2022.11.01
 * purpose: this class performs the api operations and interacts with heroku web service
 */

package edu.heinz.ds.project4task2;

import android.app.Activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * Class: GetTrades
 * Creates a BackgroundTask to call to the CongressTradingWebService and get trades from QuiverQuantAPI.
 * Source: InterestingPicture Lab
 */
public class GetTrades {
    InvestigateTrades it = null;                    // To call back to UI thread
    String ticker = null;                           // Lookup trades for ticker value
    List<CongressTrades> tradeHistory = null;       // Return list of trades
    String error = "";                                   // Error message if something goes ary

    /**
     * Searches for the trades of a specific ticker in a BackgroundTask thread.
     * @param ticker
     * @param activity
     * @param it
     */
    public void search(String ticker, Activity activity, InvestigateTrades it) {
        this.it = it;
        this.ticker = ticker;

        // catching invalid mobile user app input ... tickers should be alphabet only
        if ((ticker != null) && (!ticker.equals("")) && (ticker.matches("^[a-zA-Z]*$"))) {
            new BackgroundTask(activity).execute();
        } else { // display error for user if invalid input is entered
            it.displayError("Ticker invalid, enter a-z characters");
        }
    }

    /**
     * Class: BackgroundTask
     * Creates a thread to call API in background of UI thread.
     */
    private class BackgroundTask {

        private Activity activity;

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    // Run in Background
                    doInBackground();

                    // Run on UI Thread after BackgroundTask complete.
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute(){
            startBackground();
        }


        /**
         * Calls getTickerTrades.
         * Stores return values in class member variable.
         */
        private void doInBackground() {
            tradeHistory = getTickerTrades(ticker);
        }

        /**
         * Calls Heroku hosted web services API to get ticker trades from QuiverQuant API.
         * Returns list of recent congress trades associated with the ticker.
         * @param ticker
         * @return List of trades from congress associated with ticker.
         */
        public List<CongressTrades> getTickerTrades(String ticker){

            HttpURLConnection conn;
            int status = 0;
            Result result = new Result();

            try {
                // GET wants us to pass the name on the URL line
                URL url = new URL("https://glacial-tor-22128.herokuapp.com/api/" + ticker);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // we are sending plain text
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                // tell the server what format we want back
                conn.setRequestProperty("Accept", "text/plain");

                // wait for response
                status = conn.getResponseCode();

                // set http response code
                result.setResponseCode(status);
                // set http response message - this is just a status message
                // and not the body returned by GET
                result.setResponseText(conn.getResponseMessage());

                String responseBody = "";

                if (status == 200) {
                    responseBody = getResponseBody(conn);


                    if (responseBody.contains("error")) {
                        error = responseBody;
                    } else {

                        result.setResponseText(responseBody);

                        // Using the JSON simple library parse the string into a json object
                        Gson gson = new Gson();

                        // Get items into a collection
                        // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
                        Type collectionType = new TypeToken<ArrayList<CongressTrades>>(){}.getType();
                        tradeHistory = gson.fromJson(responseBody, collectionType);
                    }
                }

                System.out.println("getting result body: " + result.getResponseText());

                conn.disconnect();
            }
            // handle exceptions
            catch (MalformedURLException e) {
                System.out.println("URL Exception thrown" + e);
            } catch (IOException e) {
                error = "Mobile Application Network Failure. Unable to reach server.";
                System.out.println("IO Exception thrown" + e);
            } catch (Exception e) {
                System.out.println("IO Exception thrown" + e);
            }
            return tradeHistory;
        }

        /**
         * Send trades back to UI thread.
         */
        public void onPostExecute() {

            if (error.equals("")) { // if all is well, we return the trade history for congresspeople
                it.tradesReady(tradeHistory);
            } else { // if there's a network error we handle it here
                it.displayError(error);
            }
    }

    /**
     * Class: Result
     * Source: InterestingPicture Lab
     */
    class Result {
        private int responseCode;
        private String responseText;

        public int getResponseCode() { return responseCode; }
        public void setResponseCode(int code) { responseCode = code; }
        public String getResponseText() { return responseText; }
        public void setResponseText(String msg) { responseText = msg; }

        public String toString() { return responseCode + ":" + responseText; }
    }

    /**
     * Source: InterestingPicture Lab
     * @param conn
     * @return
     */
    public String getResponseBody(HttpURLConnection conn) {
        String responseText = "";
        try {
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                responseText += output;
            }
            conn.disconnect();
        } catch (IOException e) {
            System.out.println("Exception caught " + e);
        }
        return responseText;
    }

    }
}

