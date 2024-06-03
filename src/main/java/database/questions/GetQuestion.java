package database.questions;

import com.google.gson.Gson;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import connection.MongoDB;
import database.IServiceDatabase;
import lawlaboratory.models.questions.Question;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class GetQuestion implements IServiceDatabase<Question> {
    public static GetQuestion getInstance(){
        return new GetQuestion();
    }

    @Override
    public Map<String, Question> getListData(String collection) {
        MongoDB mongoDB = new MongoDB("lawlaboratory");
        MongoDatabase database = mongoDB.getDatabase();

        MongoCollection<Document> lawMongoCollection = database.getCollection(collection);
        Map<String, Question> questionMap = new HashMap<>();
        FindIterable<Document> documents = lawMongoCollection.find();

        for (Document document : documents){
            String jsonContent = document.toJson();
            Gson gson = new Gson();
            Question question = gson.fromJson(jsonContent, Question.class);
            question.setId(document.getObjectId("_id"));

            String id = document.getObjectId("_id").toString();
            questionMap.put(id, question);
        }

        mongoDB.closeConnection();

        return questionMap;
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

    public String getNearestDateAnswer(String collectionName) {
        String connectionString = "mongodb://localhost:27017";
        String nearestDateAnswer = null;
        long smallestDifference = Long.MAX_VALUE;

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("lawlaboratory");
            MongoCollection<Document> collection = database.getCollection(collectionName);

            FindIterable<Document> documents = collection.find(
                    Filters.and(
                            Filters.exists("date_answer"),
                            Filters.ne("date_answer", "")
                    )
            );

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Document doc : documents) {
                String dateAnswerStr = doc.getString("date_answer");
                if (dateAnswerStr != null && !dateAnswerStr.isEmpty()) {
                    LocalDate dateAnswer = LocalDate.parse(dateAnswerStr, formatter);
                    long difference = ChronoUnit.DAYS.between(today, dateAnswer);
                    if (Math.abs(difference) < smallestDifference) {
                        smallestDifference = Math.abs(difference);
                        nearestDateAnswer = dateAnswerStr;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nearestDateAnswer;
    }
}
