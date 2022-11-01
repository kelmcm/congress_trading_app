/**
 * Project 4, Task 1
 * Nicholas Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnection {

    static ConnectionString connectionString = new ConnectionString(
            "mongodb://ds_project4_user:" + System.getenv("MongoDBPassword") + "@" +
                    "ac-vmwvcoa-shard-00-00.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-01.efsllgj.mongodb.net:27017," +
                    "ac-vmwvcoa-shard-00-02.efsllgj.mongodb.net:27017" +
                    "/cluster0?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");

    /**
     * Main driver method to read and write to MongoDB.
     * @param args
     */
    public static void main(String[] args) {

        // Create settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(MongoDBConnection.connectionString)
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
        MongoDatabase database = mongoClient.getDatabase("test").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<UserString> collection = database.getCollection("strings", UserString.class);

        // Remove printing logging -- Not working.
        // Source: https://stackoverflow.com/questions/40780710/how-can-i-remove-the-mongodb-message-of-java-console
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        MongoDBConnection mdbc = new MongoDBConnection();

        while(!userInput.equals("exit")) {

            // Prompt user
            System.out.println("Enter a string (type 'exit' to close):");
            userInput = scanner.nextLine();

            // If user enters exit, exit the application
            if(userInput.equals("exit")) {
                System.exit(0);
            }

            // Create UserString and insert into collection
            UserString newString = new UserString(new ObjectId(), userInput);
            mdbc.insert(collection, newString);
            System.out.println();

            // Read all strings in the collection
            mdbc.readAll(collection);
            System.out.println();

        }

    }

    /**
     * Inserts an object into a collection
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/insertOne/
     * @param collection Collection to insert
     * @param object Object to insert
     */
    public void insert(MongoCollection collection, Object object) {
        try {
            InsertOneResult result = collection.insertOne(object);
            System.out.printf("Success! Inserted document id: %s\n", result.getInsertedId());
        } catch (MongoException me) {
            System.err.println("Unable to insert due to an error: " + me);
        }
    }

    /**
     * Reads all documents in a specified collection
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/usage-examples/find/
     * Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/document-data-format-pojo/
     * @param collection Collection to read
     */
    public void readAll(MongoCollection collection) {
        System.out.println("Reading all strings from the database:");
        MongoCursor iterator = collection.find().iterator();
        try {
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } finally {
            iterator.close();
        }
    }

}
