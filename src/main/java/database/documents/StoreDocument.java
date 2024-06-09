package database.documents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connection.MongoDB;
import database.IServiceDatabase;
import lawlaboratory.models.documents.Law;
import org.bson.Document;

import java.util.Map;

public class StoreDocument implements IServiceDatabase<Law> {

    public static StoreDocument getInstance(){
        return new StoreDocument();
    }

    @Override
    public Map<String, Law> getListData(String collection) {
        return null;
    }

    @Override
    public Law getAData(String id, String collection) {
        return null;
    }

    @Override
    public synchronized void save(Law law, String collection) {
        try {
            MongoDB mongoDB = new MongoDB("law_new");

            MongoDatabase database = mongoDB.getDatabase();

            MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(law);
            org.bson.Document document = org.bson.Document.parse(json);

            lawMongoCollection.insertOne(document);

            mongoDB.closeConnection();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(Law law, String collection) {

    }

    @Override
    public void delete(Law law, String collection) {

    }
}
