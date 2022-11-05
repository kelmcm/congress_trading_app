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
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "api", value = "/api/*")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() { }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        System.out.println("I am in the servlet doGet");

        String pathInfo = request.getPathInfo();
        String[] pathInfoSplit = pathInfo.split("/");
        String ticker = pathInfoSplit[pathInfoSplit.length -1];

        URL url = new URL("https://api.quiverquant.com/beta/historical/congresstrading/" + ticker);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        httpConn.setRequestProperty("accept", "application/json");
        httpConn.setRequestProperty("X-CSRFToken", "TyTJwjuEC7VV7mOqZ622haRaaUr0x0Ng4nrwSRFKQs7vdoBcJlK9qjAS69ghzhFu");
        httpConn.setRequestProperty("Authorization", "Token " + System.getenv("QuiverQuantAPIKey"));

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String apiResponse = s.hasNext() ? s.next() : "";
//        System.out.println(response);  /*un-comment for testing*/

        //Using the JSON simple library parse the string into a json object
        Gson gson = new Gson();

        // get items into a collection
        // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
        Type collectionType = new TypeToken<Collection<CongressTrading>>(){}.getType();
        List<CongressTrading> tradeHistory = gson.fromJson(apiResponse, collectionType);

        for(int i = 0; i < tradeHistory.size(); i++) {
            CongressTrading tradeResponse = tradeHistory.get(i);
            System.out.printf("%-20s %-30s %-8s \t %-28s of %-6s on %-10s\n", tradeResponse.getHouse(), tradeResponse.getRepresentative(), tradeResponse.getTransaction(), tradeResponse.getRange(), tradeResponse.getTicker(), tradeResponse.getTransactionDate());
        }

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