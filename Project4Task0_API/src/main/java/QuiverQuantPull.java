import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class QuiverQuantPull {

    public static void main(String[] args) throws IOException {

        // API pull source 1: https://www.scrapingbee.com/curl-converter/java/
        // API pull source 2: https://api.quiverquant.com/docs/
//        URL url = new URL("https://api.quiverquant.com/beta/historical/senatetrading/ge");
        URL url = new URL("https://api.quiverquant.com/beta/live/senatetrading");

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        httpConn.setRequestProperty("accept", "application/json");
        httpConn.setRequestProperty("X-CSRFToken", "TyTJwjuEC7VV7mOqZ622haRaaUr0x0Ng4nrwSRFKQs7vdoBcJlK9qjAS69ghzhFu");
        httpConn.setRequestProperty("Authorization", "Token " + System.getenv("QuiverQuantAPIKey"));

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
//        System.out.println(response);  /*un-comment for testing*/

        //Using the JSON simple library parse the string into a json object
        Gson gson = new Gson();

        // get items into a collection
        // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
        Type collectionType = new TypeToken<Collection<SenateTradingResponse>>(){}.getType();
        Collection<SenateTradingResponse> enums = gson.fromJson(response, collectionType);

        for (Iterator i = enums.iterator(); i.hasNext(); )
            System.out.println(i.next().toString());
    }
}
