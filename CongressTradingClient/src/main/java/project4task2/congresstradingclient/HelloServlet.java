package project4task2.congresstradingclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/getTickerTrades")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpURLConnection conn;
        int status = 0;
        Result result = new Result();
        String ticker = request.getParameter("ticker");

        try {
            // GET wants us to pass the name on the URL line
            URL url = new URL("http://localhost:8080/CongressTradingWebService-1.0-SNAPSHOT/api/" + ticker);
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

            String nextView = "index.jsp";
            request.setAttribute("response_text", result.getResponseText());
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);

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

    }

    public void destroy() {
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