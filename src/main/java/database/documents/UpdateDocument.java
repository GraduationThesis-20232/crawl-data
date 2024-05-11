package database.documents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import connection.MongoDB;
import database.IServiceDatabase;
import lawlaboratory.models.documents.Law;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class UpdateDocument implements IServiceDatabase<Law> {
    public static UpdateDocument getInstance() {
        return new UpdateDocument();
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
    public void save(Law law, String collection) {

    }

    @Override
    public void update(Law law, String collection) {
        try {
            MongoDB mongoDB = new MongoDB("lawnew");
            MongoDatabase database = mongoDB.getDatabase();
            MongoCollection<Document> lawMongoCollection = database.getCollection(collection);

            ObjectId lawId = law.getId();

            Document updateFields = new Document();
//            updateFields.append("legislation", law.getLegislation());
            updateFields.append("effective_area", law.getEffective_area());
            updateFields.append("source_collection", law.getSource_collection());
//            updateFields.append("ministries", law.getMinistries());
            updateFields.append("field", law.getField());
//            updateFields.append("issuing_body", law.getIssuing_body());
            updateFields.append("chairwoman", law.getChairwoman());
            updateFields.append("signer", law.getSigner());
//            updateFields.append("issued_date", law.getIssued_date());
            updateFields.append("effective_date", law.getEffective_date());
            updateFields.append("gazette_date", law.getGazette_date());
            updateFields.append("effect_status", law.getEffect_status());
//            updateFields.append("source_url", law.getSource_url());
            updateFields.append("reason_expiration", law.getReason_expiration());
            updateFields.append("expiry_date", law.getExpiry_date());


            Document updateQuery = new Document("_id", lawId);
            Document updateDocument = new Document("$set", updateFields);
            lawMongoCollection.updateOne(updateQuery, updateDocument);

            mongoDB.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Law law, String collection) {

    }
}
