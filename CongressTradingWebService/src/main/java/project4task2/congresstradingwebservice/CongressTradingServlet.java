/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

package project4task2.congresstradingwebservice;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

/**
 * Class: CongressTradingServlet
 *
 * - Creates API that sends HTTP request to the QuiverQuantAPI based on the specified ticker value and returns response to the Client.
 * - Connects dashboard.jsp to URL path to pull metrics from MongoDB and display on dashboard.
 */
@WebServlet(name = "api", urlPatterns = {"/api/*", "/dashboard"})
public class CongressTradingServlet extends HttpServlet {

    CongressTradingModel model;

    /**
     * Instantiates model on servlet creation
     */
    public void init() {
        model = new CongressTradingModel();
    }

    /**
     * Handles HTTP GET requests for api and dashboard paths.
     * @param request
     * @param response
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        if(request.getServletPath().contains("/api")) {

            model.callAPI(request, response);

        // If dashboard URL, calculate analytics and display dashboard
        } else if(request.getServletPath().contains("/dashboard")) {

            RequestDispatcher view = model.displayDashboard(request);

            try {
                view.forward(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.out.println("Error forwarding to next view.");
            }
        }

    }

    public void destroy() {

    }

}