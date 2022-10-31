import org.bson.types.ObjectId;

/**
 * Project 4, Task 1
 * Cole Thomas, nhthomas
 * Kelly McManus, kellymcm
 */

// Source: https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/data-formats/document-data-format-pojo/
public class UserString {

    private ObjectId id;
    private String string;

    public UserString() {}

    public UserString(ObjectId id, String string) {
        this.id = id;
        this.string = string;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return String.format("User String [id = %s, string = %s]", id, string);
    }


}
