/**
 * Project 4, Part 1, Fetch API
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Fetcher {

    // Source: https://www.baeldung.com/java-http-request
    public static void main(String[] args) {

        Fetcher fetcher = new Fetcher();
        String api_key = "e3523da4-1238-4e02-a3b2-6cfc94f9bc78";
        String country = "CA";
        String year = "2021";
        HttpURLConnection con = null;

        /**
         * API key: e3523da4-1238-4e02-a3b2-6cfc94f9bc78
         * https://holidayapi.com/v1/holidays?pretty&key=e3523da4-1238-4e02-a3b2-6cfc94f9bc78&country=US&year=2021
         */
        try {
            URL url = new URL("https://holidayapi.com/v1/holidays?pretty&key=" + api_key + "&country=" + country + "&year=" + year);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Make API request
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // Read response
            String inputLine;
            StringBuffer content = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            System.out.println(content);

        } catch (MalformedURLException e) {
            System.out.println("Bad URL.");
        } catch (ProtocolException e2) {
            System.out.println("Error in protocol");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            con.disconnect();
        }

    }

}
