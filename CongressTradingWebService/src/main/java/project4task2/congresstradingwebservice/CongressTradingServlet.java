/**
 * Project 4, Task 2
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

package project4task2.congresstradingwebservice;

import java.io.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Class: CongressTradingServlet
 *
 * - Creates API that sends HTTP request to the QuiverQuantAPI based on the specified ticker value and returns response to the Client.
 * - Connects dashboard.jsp to URL path to pull metrics from MongoDB and display on dashboard.
 */
@WebServlet(name = "api", urlPatterns = {"/api/*", "/dashboard"})
public class CongressTradingServlet extends HttpServlet {

    // Instantiate string to connect to MongoDB. Requires password stored in environment variable.
    static ConnectionString connectionString = new ConnectionString(
            "mongodb://ds_project4_user:" + System.getenv("MongoDBPassword") + "@" +
                    "ac-vmwvcoa-shard-00-00.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-01.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-02.efsllgj.mongodb.net:27017" +
                    "/cluster0?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");

    // Instantiate MongoDB collection objects
    MongoCollection<TradeRequest> collectionTradeRequest;
    MongoCollection<APIRequest> collectionAPIRequest;
    MongoCollection<HerokuResponse> collectionHerokuResponse;

    public void init() {

        // Create settings object for MongoDB
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(CongressTradingServlet.connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        // Register POJOs with MongoDB in order to read and write between MongoDB and POJOs
        // Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/document-data-format-pojo/
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        // Connect to database and collection
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("CongressTrading").withCodecRegistry(pojoCodecRegistry);
        collectionTradeRequest = database.getCollection("TradeRequest", TradeRequest.class);
        collectionAPIRequest = database.getCollection("APIRequest", APIRequest.class);
        collectionHerokuResponse = database.getCollection("HerokuResponse", HerokuResponse.class);

    }

    /**
     * Handles HTTP GET requests for api and dashboard paths.
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(request.getServletPath().contains("/api")) {

            // Extract ticker value from path URL
            String pathInfo = request.getPathInfo();
            String[] pathInfoSplit = pathInfo.split("/");
            String ticker = pathInfoSplit[pathInfoSplit.length -1];

            // Extract trade request data
            String mobileDevice = request.getHeader("User-Agent");
            String requestMethod = request.getHeader("Access-Control-Request-Method");
            Date requestedAt = new Date();

            // Store trade request data in MongoDB for analysis
            TradeRequest tradeRequest = new TradeRequest(new ObjectId(), mobileDevice, requestMethod, ticker, requestedAt);
            CongressTradingServlet congressTradingServlet = new CongressTradingServlet();
            congressTradingServlet.insert(collectionTradeRequest, tradeRequest);

            // Build QuiverQuantAPI URL to ping
            URL url = new URL("https://api.quiverquant.com/beta/historical/congresstrading/" + ticker);

            // Create HTTP Connection
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");

            // Set QuiverQuantAPI properties and API key
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.setRequestProperty("X-CSRFToken", "TyTJwjuEC7VV7mOqZ622haRaaUr0x0Ng4nrwSRFKQs7vdoBcJlK9qjAS69ghzhFu");
            httpConn.setRequestProperty("Authorization", "Token " + System.getenv("QuiverQuantAPIKey"));

            // Store start time
            long startTime = System.currentTimeMillis();

            // If response is 200, get input stream, else get error stream
            InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                    ? httpConn.getInputStream()
                    : httpConn.getErrorStream();

            // Read response stream
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String apiResponse = s.hasNext() ? s.next() : "";

            // Store end time and calculate process time from API
            long endTime = System.currentTimeMillis();
            APIRequest apiRequest = new APIRequest(new ObjectId(), endTime - startTime);
            congressTradingServlet.insert(collectionAPIRequest, apiRequest);

            // Instantiate gson to parse JSON to POJO
            Gson gson = new Gson();

            // Extract CongressTrading objects into list
            // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
            Type collectionType = new TypeToken<ArrayList<CongressTrade>>(){}.getType();
            List<CongressTrade> tradeHistory = gson.fromJson(apiResponse, collectionType);

            // Store response value in MongoDB for analysis
            HerokuResponse herokuResponse = new HerokuResponse(new ObjectId(), tradeHistory.size());
            congressTradingServlet.insert(collectionHerokuResponse, herokuResponse);

            // Print API response items to console
            for(int i = 0; i < tradeHistory.size(); i++) {
                CongressTrade tradeResponse = tradeHistory.get(i);
                System.out.printf("%-20s %-30s %-8s \t %-28s of %-6s on %-10s\n", tradeResponse.getHouse(), tradeResponse.getRepresentative(), tradeResponse.getTransaction(), tradeResponse.getRange(), tradeResponse.getTicker(), tradeResponse.getTransactionDate());
            }

            // Write web service response back to Client
            // Source: https://www.baeldung.com/servlet-json-response
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(tradeHistory);
            out.flush();

        // If dashboard URL, calculate analytics and display dashboard
        } else if(request.getServletPath().contains("/dashboard")) {

            // Read stored data in MongoDB to map of lists
            Map<String, List> mdbr = readAll();

            // Calculate TradeRequest analytics
            Map<String, Integer> mapMobileDeviceCount = new HashMap<>();
            Map<String, Integer> mapTickers = new HashMap<>();
            Map<String, Integer> mapRequestMethod = new HashMap<>();
            int totalRequest = mdbr.get("TradeRequest").size();

            // For each trade request
            for (Object object : mdbr.get("TradeRequest")) {
                TradeRequest tradeRequest = (TradeRequest) object;

                // Count mobile devices
                if(mapMobileDeviceCount.containsKey(tradeRequest.getMobileDevice())) {
                    mapMobileDeviceCount.put(tradeRequest.getMobileDevice(), mapMobileDeviceCount.get(tradeRequest.getMobileDevice()) + 1);
                } else {
                    mapMobileDeviceCount.put(tradeRequest.getMobileDevice(), 1);
                }

                // Count tickers
                if(mapTickers.containsKey(tradeRequest.getTicker())) {
                    mapTickers.put(tradeRequest.getTicker(), mapTickers.get(tradeRequest.getTicker()) + 1);
                } else {
                    mapTickers.put(tradeRequest.getTicker(), 1);
                }

                // Count request methods
                if(mapRequestMethod.containsKey(tradeRequest.getRequestMethod())) {
                    mapRequestMethod.put(tradeRequest.getRequestMethod(), mapRequestMethod.get(tradeRequest.getRequestMethod()) + 1);
                } else {
                    mapRequestMethod.put(tradeRequest.getRequestMethod(), 1);
                }

            }

            // Set attributes back to request
            request.setAttribute("mapMobileDevice", mapMobileDeviceCount.toString());
            request.setAttribute("mapTickers", mapTickers.toString());
            request.setAttribute("mapRequestMethod", mapRequestMethod.toString());
            request.setAttribute("totalRequests", totalRequest);
            request.setAttribute("tradeRequestLog", mdbr.get("TradeRequest"));

            // Get APIRequest data
            long apiResponseSum = 0;
            int apiResponseCount = 0;

            // For each APIRequest
            for (Object object : mdbr.get("APIRequest")) {
                APIRequest apiRequest = (APIRequest) object;

                // Sum processing time
                apiResponseSum += apiRequest.getProcessTime();
                apiResponseCount++;
            }

            // Set attribute for average API response time
            request.setAttribute("averageAPIResponseTime", apiResponseSum / apiResponseCount);
            request.setAttribute("apiRequestLog", mdbr.get("APIRequest"));

            // Get HerokuResponse data
            long recordsSum = 0;
            int recordsCount = 0;

            // For each Heroku Response
            for (Object object : mdbr.get("HerokuResponse")) {
                HerokuResponse herokuResponse = (HerokuResponse) object;

                // Sum all records
                recordsSum += herokuResponse.getNumberOfRecords();
                recordsCount++;
            }

            // Set attribute for average records
            request.setAttribute("averageRecords", recordsSum / recordsCount);
            request.setAttribute("herokuResponseLog", mdbr.get("HerokuResponse"));

            // Create and forward to new view
            RequestDispatcher view = request.getRequestDispatcher("dashboard.jsp");
            try {
                view.forward(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void destroy() {
    }


    /**
     * Inserts an object into a collection
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/insertOne/
     * @param collection Collection to insert
     * @param object Object to insert
     */
    public void insert(MongoCollection collection, Object object) {
        try {
            System.out.println("I am in try");
            InsertOneResult result = collection.insertOne(object);
            System.out.printf("Success! Inserted document id: %s\n", result.getInsertedId());
        } catch (Exception me) {
            System.err.println("Unable to insert due to an error: " + me);
        }
    }

    /**
     * Reads all documents in a specified collection
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/find/
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/document-data-format-pojo/
     */
    public Map<String, List> readAll() {

        List<TradeRequest> docsTradingRequest = collectionTradeRequest.find(new Document(), TradeRequest.class).into(new ArrayList<TradeRequest>());
        List<APIRequest> docsAPIRequest = collectionAPIRequest.find(new Document(), APIRequest.class).into(new ArrayList<APIRequest>());
        List<HerokuResponse> docsHerokuResponse = collectionHerokuResponse.find(new Document(), HerokuResponse.class).into(new ArrayList<HerokuResponse>());

        Map<String, List> map = new HashMap();
        map.put("TradeRequest", docsTradingRequest);
        map.put("APIRequest", docsAPIRequest);
        map.put("HerokuResponse", docsHerokuResponse);

        return map;
    }

    public void getTickerResponse(HttpServletRequest request, HttpServletResponse response) {


    }

}