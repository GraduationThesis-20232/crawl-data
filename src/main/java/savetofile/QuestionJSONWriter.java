package savetofile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lawlaboratory.models.questions.Question;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuestionJSONWriter {
    public static QuestionJSONWriter getInstance() {
        return new QuestionJSONWriter();
    }

    public synchronized void saveToJSON(Question question, String fileName) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(question);

        String today = LocalDate.now().toString();
        String directoryPath = "src/main/resources/data/questions/" + today;
        String filePath = directoryPath + "/" + fileName;

        try {
            Path dirPath = Paths.get(directoryPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))){
            out.println(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Question> readQuestionsFromJson(String filePath) throws FileNotFoundException {
        List<Question> questions = new ArrayList<>();
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Question question = gson.fromJson(line, Question.class);
                    questions.add(question);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return questions;
    }
}

