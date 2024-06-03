package savetofile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lawlaboratory.models.questions.Question;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class QuestionJSONWriter {

    public static synchronized void saveToJSON(Question question, String filePath) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(question);

        try (FileOutputStream fos = new FileOutputStream(filePath, true);
             FileChannel fileChannel = fos.getChannel();
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             PrintWriter out = new PrintWriter(osw)) {

            try (FileLock lock = fileChannel.lock()) {
                out.println(json);
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

