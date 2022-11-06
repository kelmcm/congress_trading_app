package edu.heinz.ds.androidinterestingpicture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;
//import android.os.Build;
//import android.support.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.annotation.RequiresApi;

/*
 * This class provides capabilities to search for an image on Flickr.com given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of inner class BackgroundTask that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 * 
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is
 *    done; it calls *back* to ip to report the results
 *
 */
public class GetPicture {
    InterestingPicture ip = null;   // for callback
    String searchTerm = null;       // search Flickr for this word
    Bitmap picture = null;          // returned from Flickr
    String tradeResponseText = null;

    // search( )
    // Parameters:
    // String searchTerm: the thing to search for on flickr
    // Activity activity: the UI thread activity
    // InterestingPicture ip: the callback method's class; here, it will be ip.pictureReady( )
    public void search(String searchTerm, Activity activity, InterestingPicture ip) {
        this.ip = ip;
        this.searchTerm = searchTerm;
        new BackgroundTask(activity).execute();
    }

    // class BackgroundTask
    // Implements a background thread for a long running task that should not be
    //    performed on the UI thread. It creates a new Thread object, then calls doInBackground() to
    //    actually do the work. When done, it calls onPostExecute(), which runs
    //    on the UI thread to update some UI widget (***never*** update a UI
    //    widget from some other thread!)
    //
    // Adapted from one of the answers in
    // https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
    // Modified by Barrett
    //
    // Ideally, this class would be abstract and parameterized.
    // The class would be something like:
    //      private abstract class BackgroundTask<InValue, OutValue>
    // with two generic placeholders for the actual input value and output value.
    // It would be instantiated for this program as
    //      private class MyBackgroundTask extends BackgroundTask<String, Bitmap>
    // where the parameters are the String url and the Bitmap image.
    //    (Some other changes would be needed, so I kept it simple.)
    //    The first parameter is what the BackgroundTask looks up on Flickr and the latter
    //    is the image returned to the UI thread.
    // In addition, the methods doInBackground() and onPostExecute( ) could be
    //    absttract methods; would need to finesse the input and ouptut values.
    // The call to activity.runOnUiThread( ) is an Android Activity method that
    //    somehow "knows" to use the UI thread, even if it appears to create a
    //    new Runnable.

    private class BackgroundTask {

        private Activity activity; // The UI thread

        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        private void execute(){
            // There could be more setup here, which is why
            //    startBackground is not called directly
            startBackground();
        }

        // doInBackground( ) implements whatever you need to do on
        //    the background thread.
        // Implement this method to suit your needs
        private void doInBackground() {
            tradeResponseText = getTickerTrades(searchTerm);
        }

        public String getTickerTrades(String ticker){

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

                if (status == 200) {
                    String responseBody = getResponseBody(conn);
                    result.setResponseText(responseBody);
                }

                System.out.println(result.getResponseText());

                conn.disconnect();
            }
            // handle exceptions
            catch (MalformedURLException e) {
                System.out.println("URL Exception thrown" + e);
            } catch (IOException e) {
                System.out.println("IO Exception thrown" + e);
            } catch (Exception e) {
                System.out.println("IO Exception thrown" + e);
            }
            return result.getResponseText();
        }


        // onPostExecute( ) will run on the UI thread after the background
        //    thread completes.
        // Implement this method to suit your needs
        public void onPostExecute() {
            ip.tradesReady(tradeResponseText);
        }
        /*
         * Search Flickr.com for the searchTerm argument, and return a Bitmap that can be put in an ImageView
         */
        private Bitmap search(String searchTerm) {
            String pictureURL = null;
            // Debugging:
            //System.out.println("Search, searchTerm = " + searchTerm);
            // Add your Flickr key inside the quotes:
            String api_key = "adce2da2ae322035b2f732828265e59f";
            // Call Flickr to get the web page containing image URL's of the search term
            Document doc =
                    getRemoteXML("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" +
                            api_key+
                            "&is_getty=true&tags="+searchTerm);

            // Get the photo element
            NodeList nl = doc.getElementsByTagName("photo");
            if (nl.getLength() == 0) {
                return null; // no pictures found
            } else {
                int pic = new Random().nextInt(nl.getLength()); //choose a random picture
                Element e = (Element) nl.item(pic);
                String farm = e.getAttribute("farm");
                String server = e.getAttribute("server");
                String id = e.getAttribute("id");
                String secret = e.getAttribute("secret");
                // Note: http will fail in the search method, but gives an
                //    error on the BitMapFactory call (???)
                pictureURL = "https://farm"+farm+".static.flickr.com/"+server+"/"+id+"_"+secret+"_z.jpg";
            }
            // At this point, we have the URL of the picture that resulted from the search.  Now load the image itself.
            try {
                URL u = new URL(pictureURL);
                // Debugging:
                //System.out.println(pictureURL);
                return getRemoteImage(u);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // so compiler does not complain
            }

        }

        /*
         * Given a url that will request XML, return a Document with that XML, else null
         */
        private Document getRemoteXML(String url) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource(url);
                return db.parse(is);
            } catch (Exception e) {
                System.out.print("Yikes, hit the error: "+e);
                return null;
            }
        }

        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        @RequiresApi(api = Build.VERSION_CODES.P)
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // A simple class to wrap an RPC result.
    class Result {
        private int responseCode;
        private String responseText;

        public int getResponseCode() { return responseCode; }
        public void setResponseCode(int code) { responseCode = code; }
        public String getResponseText() { return responseText; }
        public void setResponseText(String msg) { responseText = msg; }

        public String toString() { return responseCode + ":" + responseText; }
    }

    // Gather up a response body from the connection
    // and close the connection.
    public static String getResponseBody(HttpURLConnection conn) {
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

