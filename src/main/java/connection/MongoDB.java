package connection;

import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDB implements ConnectionDB {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDB(String databaseName) {
        openConnection(databaseName);
//        closeConnection();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void openConnection(String databaseName) {
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");

        mongoClient = new MongoClient(uri);

        database = mongoClient.getDatabase(databaseName);
    }

    @Override
    public void closeConnection() {
        try {
            mongoClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
