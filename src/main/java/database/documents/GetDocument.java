package database.documents;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connection.MongoDB;
import database.IServiceDatabase;
import lawlaboratory.models.documents.Law;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetDocument implements IServiceDatabase<Law> {
    public static GetDocument getInstance(){
        return new GetDocument();
    }

    @Override
    public Map<String, Law> getListData(String collection) {
        MongoDB mongoDB = new MongoDB("lawnew");

        MongoDatabase database = mongoDB.getDatabase();

        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

        Map<String, Law> lawsMap = new HashMap<>();

        FindIterable<Document> documents = lawMongoCollection.find();
        for (Document document : documents){
            String jsonContent = document.toJson();
            Gson gson = new Gson();
            Law law = gson.fromJson(jsonContent, Law.class);
            law.setId(document.getObjectId("_id"));

            String id = document.getObjectId("_id").toString();

            lawsMap.put(id, law);
        }

        mongoDB.closeConnection();

        return lawsMap;
    }

    @Override
    public Law getAData(String id, String collection) {
        MongoDB mongoDB = new MongoDB("lawnew");
        MongoDatabase database = mongoDB.getDatabase();
        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

        Document query = new Document("_id", new ObjectId(id));
        Document result = lawMongoCollection.find(query).first();

        if (result != null) {
            String jsonContent = result.toJson();
            Gson gson = new Gson();
            Law law = gson.fromJson(jsonContent, Law.class);
            law.setId(result.getObjectId("_id"));
            return law;
        } else {
            return null;
        }
    }

    @Override
    public void save(Law law, String collection) {

    }

    @Override
    public void update(Law law, String collection) {

    }

    @Override
    public void delete(Law law, String collection) {

    }

    public Law getDocumentByIdentifier(String identifier, String collection) {
        MongoDB mongoDB = new MongoDB("lawnew");
        MongoDatabase database = mongoDB.getDatabase();
        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

        Document query = new Document("identifier", identifier);
        Document result = lawMongoCollection.find(query).first();

        if (result != null) {
            String jsonContent = result.toJson();
            Gson gson = new Gson();
            Law law = gson.fromJson(jsonContent, Law.class);
            law.setId(result.getObjectId("_id"));
            return law;
        } else {
            return null;
        }
    }
}
