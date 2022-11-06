/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

package project4task2.congresstradingwebservice;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "api", value = "/api/*")
public class HelloServlet extends HttpServlet {

    public void init() { }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Extract ticker value from path URL
        String pathInfo = request.getPathInfo();
        String[] pathInfoSplit = pathInfo.split("/");
        String ticker = pathInfoSplit[pathInfoSplit.length -1];

        // Build QuiverQuantAPI URL to ping
        URL url = new URL("https://api.quiverquant.com/beta/historical/congresstrading/" + ticker);

        // Create HTTP Connection
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        // Set QuiverQuantAPI properties and API key
        httpConn.setRequestProperty("accept", "application/json");
        httpConn.setRequestProperty("X-CSRFToken", "TyTJwjuEC7VV7mOqZ622haRaaUr0x0Ng4nrwSRFKQs7vdoBcJlK9qjAS69ghzhFu");
        httpConn.setRequestProperty("Authorization", "Token " + System.getenv("QuiverQuantAPIKey"));

        // If response is 200, get input stream, else get error stream
        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String apiResponse = s.hasNext() ? s.next() : "";

        // Using the JSON simple library parse the string into a json object
        Gson gson = new Gson();

        // Get items into a collection
        // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
        Type collectionType = new TypeToken<ArrayList<CongressTrading>>(){}.getType();
        List<CongressTrading> tradeHistory = gson.fromJson(apiResponse, collectionType);

        // Source: https://howtodoinjava.com/gson/gson-parse-json-array/
//        CongressTrading[] congressTradings = gson.fromJson(apiResponse, CongressTrading[].class);
//
//        // Print API response items to console
        for(int i = 0; i < tradeHistory.size(); i++) {
            CongressTrading tradeResponse = tradeHistory.get(i);
            System.out.printf("%-20s %-30s %-8s \t %-28s of %-6s on %-10s\n", tradeResponse.getHouse(), tradeResponse.getRepresentative(), tradeResponse.getTransaction(), tradeResponse.getRange(), tradeResponse.getTicker(), tradeResponse.getTransactionDate());
        }

        // Write web service response back to Client
        // Source: https://www.baeldung.com/servlet-json-response
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(tradeHistory);
        out.flush();

    }

    public void destroy() {
    }
}