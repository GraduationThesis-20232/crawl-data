package database.questions;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import database.IServiceDatabase;
import lawlaboratory.models.questions.Question;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.Map;

public class UpdateQuestion implements IServiceDatabase<Question> {
    public static UpdateQuestion getInstance(){
        return new UpdateQuestion();
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
    public void save(Question question, String collection) {

    }

    @Override
    public void update(Question question, String collection) {

    }

    @Override
    public void delete(Question question, String collection) {

    }

    public void updateDateAnswer(String collectionName) {
        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("lawlaboratory");
            MongoCollection<Document> collection = database.getCollection(collectionName);

            Bson filter =  Filters.or(
                    Filters.eq("date_answer", null),
                    Filters.eq("date_answer", "")
            );

            String newDateAnswer = LocalDate.now().toString();
            Bson update = Updates.set("date_answer", newDateAnswer);

            collection.updateMany(filter, update);
        }
    }
}
