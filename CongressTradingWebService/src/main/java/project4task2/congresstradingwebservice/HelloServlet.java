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


@WebServlet(name = "api", urlPatterns = {"/api/*", "/dashboard"})
public class HelloServlet extends HttpServlet {

    static ConnectionString connectionString = new ConnectionString(
            "mongodb://ds_project4_user:" + System.getenv("MongoDBPassword") + "@" +
                    "ac-vmwvcoa-shard-00-00.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-01.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-02.efsllgj.mongodb.net:27017" +
                    "/cluster0?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");

    // Instantiate Mongo collections
    MongoCollection<TradeRequest> collectionTradeRequest;
    MongoCollection<APIRequest> collectionAPIRequest;
    MongoCollection<HerokuResponse> collectionHerokuResponse;

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Create settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(HelloServlet.connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        // Register POJO
        // Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/document-data-format-pojo/
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        // Connect to database and collection
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("CongressTrading").withCodecRegistry(pojoCodecRegistry);
        collectionTradeRequest = database.getCollection("TradeRequest", TradeRequest.class);
        collectionAPIRequest = database.getCollection("APIRequest", APIRequest.class);
        collectionHerokuResponse = database.getCollection("HerokuResponse", HerokuResponse.class);


        if(request.getServletPath().contains("/api")) {

            // Extract ticker value from path URL
            String pathInfo = request.getPathInfo();
            String[] pathInfoSplit = pathInfo.split("/");
            String ticker = pathInfoSplit[pathInfoSplit.length -1];

            // Store trade request values in MongoDB
            String mobileDevice = request.getHeader("User-Agent");
            String requestMethod = request.getHeader("Access-Control-Request-Method");
            Date requestedAt = new Date();
            TradeRequest tradeRequest = new TradeRequest(new ObjectId(), mobileDevice, requestMethod, ticker, requestedAt);
            HelloServlet helloServlet = new HelloServlet();
            helloServlet.insert(collectionTradeRequest, tradeRequest);

            // Build QuiverQuantAPI URL to ping
            URL url = new URL("https://api.quiverquant.com/beta/historical/congresstrading/" + ticker);

            // Create HTTP Connection
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");

            // Set QuiverQuantAPI properties and API key
            httpConn.setRequestProperty("accept", "application/json");
            httpConn.setRequestProperty("X-CSRFToken", "TyTJwjuEC7VV7mOqZ622haRaaUr0x0Ng4nrwSRFKQs7vdoBcJlK9qjAS69ghzhFu");
            httpConn.setRequestProperty("Authorization", "Token " + System.getenv("QuiverQuantAPIKey"));

            long startTime = System.currentTimeMillis();

            // If response is 200, get input stream, else get error stream
            InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                    ? httpConn.getInputStream()
                    : httpConn.getErrorStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String apiResponse = s.hasNext() ? s.next() : "";

            long endTime = System.currentTimeMillis();
            APIRequest apiRequest = new APIRequest(new ObjectId(), endTime - startTime);
            helloServlet.insert(collectionAPIRequest, apiRequest);

            // Using the JSON simple library parse the string into a json object
            Gson gson = new Gson();

            // Get items into a collection
            // source: https://stackoverflow.com/questions/9598707/gson-throwing-expected-begin-object-but-was-begin-array
            Type collectionType = new TypeToken<ArrayList<CongressTrading>>(){}.getType();
            List<CongressTrading> tradeHistory = gson.fromJson(apiResponse, collectionType);

            HerokuResponse herokuResponse = new HerokuResponse(new ObjectId(), tradeHistory.size());
            helloServlet.insert(collectionHerokuResponse, herokuResponse);

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

        } else {

            Map<String, List> mdbr = readAll();

            // Get trade request data
            Map<String, Integer> mapMobileDeviceCount = new HashMap<>();
            Map<String, Integer> mapTickers = new HashMap<>();
            Map<String, Integer> mapRequestMethod = new HashMap<>();
            int totalRequest = mdbr.get("TradeRequest").size();

            for (Object object : mdbr.get("TradeRequest")) {
                TradeRequest tradeRequest = (TradeRequest) object;

                if(mapMobileDeviceCount.containsKey(tradeRequest.getMobileDevice())) {
                    mapMobileDeviceCount.put(tradeRequest.getMobileDevice(), mapMobileDeviceCount.get(tradeRequest.getMobileDevice()) + 1);
                } else {
                    mapMobileDeviceCount.put(tradeRequest.getMobileDevice(), 1);
                }

                if(mapTickers.containsKey(tradeRequest.getTicker())) {
                    mapTickers.put(tradeRequest.getTicker(), mapTickers.get(tradeRequest.getTicker()) + 1);
                } else {
                    mapTickers.put(tradeRequest.getTicker(), 1);
                }

                if(mapRequestMethod.containsKey(tradeRequest.getRequestMethod())) {
                    mapRequestMethod.put(tradeRequest.getRequestMethod(), mapRequestMethod.get(tradeRequest.getRequestMethod()) + 1);
                } else {
                    mapRequestMethod.put(tradeRequest.getRequestMethod(), 1);
                }

            }

            request.setAttribute("mapMobileDevice", mapMobileDeviceCount.toString());
            request.setAttribute("mapTickers", mapTickers.toString());
            request.setAttribute("mapRequestMethod", mapRequestMethod.toString());
            request.setAttribute("totalRequests", totalRequest);

            // Get api request data
            long apiResponseSum = 0;
            int apiResponseCount = 0;

            for (Object object : mdbr.get("APIRequest")) {
                APIRequest apiRequest = (APIRequest) object;
                apiResponseSum += apiRequest.getProcessTime();
                apiResponseCount++;
            }

            request.setAttribute("averageAPIResponseTime", apiResponseSum / apiResponseCount);

            // Get api request data
            long recordsSum = 0;
            int recordsCount = 0;

            for (Object object : mdbr.get("HerokuResponse")) {
                HerokuResponse herokuResponse = (HerokuResponse) object;
                recordsSum += herokuResponse.getNumberOfRecords();
                recordsCount++;
            }

            request.setAttribute("averageRecords", recordsSum / recordsCount);

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

}