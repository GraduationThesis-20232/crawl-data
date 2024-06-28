package database.questions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connection.MongoDB;
import database.IServiceDatabase;
import lawlaboratory.models.questions.Question;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveQuestion implements IServiceDatabase<Question> {
    public static SaveQuestion getInstance(){
        return new SaveQuestion();
    }

    @Override
    public Map<String, Question> getListData(String collection) {
        return null;
    }

    @Override
    public Question getAData(String id, String collection) {
        return null;
    }

    @Override
    public synchronized void save(Question question, String collection) {
        MongoDB mongoDB = new MongoDB("lawlaboratory");

        MongoDatabase database = mongoDB.getDatabase();

        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(question);
        org.bson.Document document = org.bson.Document.parse(json);

        lawMongoCollection.insertOne(document);

        mongoDB.closeConnection();
    }

    public synchronized void save(List<Question> questions, String collection) {
        MongoDB mongoDB = new MongoDB("lawlaboratory");

        MongoDatabase database = mongoDB.getDatabase();

        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

        Gson gson = new GsonBuilder().create();
        List<Document> documents = new ArrayList<>();
        for (Question question : questions) {
            String json = gson.toJson(question);
            Document document = Document.parse(json);
            documents.add(document);
        }
        lawMongoCollection.insertMany(documents);

        mongoDB.closeConnection();
    }

    @Override
    public void update(Question question, String collection) {

    }

    @Override
    public void delete(Question question, String collection) {

    }
}
