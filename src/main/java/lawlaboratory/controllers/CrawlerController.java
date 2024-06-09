package lawlaboratory.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import crawler.documents.AttributeIntegration;
import crawler.documents.CodesCrawler;
import crawler.documents.ConstitutionCrawler;
import crawler.documents.LawCrawler;
import crawler.questions.QuestionsCrawler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import savetofile.LogCrawlManager;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
public class CrawlerController {
    public static void main(String[] args) throws IOException, ParseException {
//        CodesCrawler codesCrawler = new CodesCrawler();
//        codesCrawler.start();
//
//        LawCrawler lawCrawler = new LawCrawler();
//        lawCrawler.start();
//
//        ConstitutionCrawler constitutionCrawler = new ConstitutionCrawler();
//        constitutionCrawler.start();

//        QuestionsCrawler questionsCrawler = new QuestionsCrawler();
//        questionsCrawler.start();

//        AttributeIntegration a = new AttributeIntegration();
//        a.start();
    }

    @PostMapping("/crawl/documents/start")
    public ResponseEntity<String> crawlDocumentsStart() throws IOException, ParseException {
        try {
            ConstitutionCrawler constitutionCrawler = new ConstitutionCrawler();
            constitutionCrawler.start();

            CodesCrawler codesCrawler = new CodesCrawler();
            codesCrawler.start();

            LawCrawler lawCrawler = new LawCrawler();
            lawCrawler.start();

//        AttributeIntegration a = new AttributeIntegration();
//        a.start();

            Gson gson = new Gson();
            JsonObject response = new JsonObject();
            response.addProperty("status", "STARTED");
            response.addProperty("message", "Crawling laws started");

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/crawl/documents/in_progress")
    public ResponseEntity<String> crawlDocumentsInProgress() throws IOException, ParseException {
        try {
            LocalDate today = LocalDate.now();
            ArrayList<String> logs = LogCrawlManager.getInstance().getLogDocumentCrawled(today.toString() + ".txt");

            Gson gson = new Gson();
            JsonObject response = new JsonObject();
            if (logs.size() == 3) {
                response.addProperty("status", "DONE");
                response.addProperty("message", "Crawling laws done.");
            } else {
                response.addProperty("status", "IN_PROGRESS");
                response.addProperty("message", "Crawling laws in progress.");
            }
            response.addProperty("numberTypeCrawled", logs.size());

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/crawl/documents/done")
    public ResponseEntity<String> crawlDocumentsDone() throws IOException, ParseException {
        try {
            LogCrawlManager logCrawlManager = LogCrawlManager.getInstance();
            ArrayList<String> logs = logCrawlManager.getLogDocumentCrawled(logCrawlManager.findNearestLogFile("src/main/resources/data/documents/log"));

            ArrayList<JsonObject> responseLogs = new ArrayList<>();
            for (String log: logs) {
                String[] parts = log.split("\\s+");
                String dateTimeString = parts[0] + " " + parts[1];
                String numberDocumentsString = parts[2];
                String type = log.substring(dateTimeString.length() + numberDocumentsString.length() + 2);

                JsonObject response = new JsonObject();
                response.addProperty("dateTime", dateTimeString);
                response.addProperty("count", Integer.parseInt(numberDocumentsString));
                response.addProperty("type", type);
                responseLogs.add(response);
            }

            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(responseLogs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/crawl/questions/start")
    public ResponseEntity<String> crawlQuestionsStart() throws IOException, ParseException {
        try {
            QuestionsCrawler questionsCrawler = new QuestionsCrawler();
            questionsCrawler.start();

            Gson gson = new Gson();
            JsonObject response = new JsonObject();
            response.addProperty("status", "STARTED");
            response.addProperty("message", "Crawling questions started");

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/crawl/questions/in_progress")
    public ResponseEntity<String> crawlQuestionsInProgress() throws IOException, ParseException {
        try {
            LocalDate today = LocalDate.now();
            ArrayList<String> logs = LogCrawlManager.getInstance().getLogQuestionsCrawled(today.toString() + ".txt");

            Gson gson = new Gson();
            JsonObject response = new JsonObject();
            if (logs.size() == 26 ) {
                response.addProperty("status", "DONE");
                response.addProperty("message", "Crawling questions done.");
            } else {
                response.addProperty("status", "IN_PROGRESS");
                response.addProperty("message", "Crawling questions in progress.");
            }
            response.addProperty("numberFieldCrawled", logs.size());

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/crawl/questions/done")
    public ResponseEntity<String> crawlQuestionsDone() throws IOException, ParseException {
        try {
            LogCrawlManager logCrawlManager = LogCrawlManager.getInstance();
            ArrayList<String> logs = logCrawlManager.getLogQuestionsCrawled(logCrawlManager.findNearestLogFile("src/main/resources/data/questions/log"));

            ArrayList<JsonObject> responseLogs = new ArrayList<>();
            for (String log: logs) {
                String[] parts = log.split("\\s+");
                String dateTimeString = parts[0] + " " + parts[1];
                String fileName = parts[2];
                String numberQuestionsString = parts[3];
                String field = log.substring(dateTimeString.length() + fileName.length() + numberQuestionsString.length() + 3);

                if (numberQuestionsString.equals("0")) continue;

                JsonObject response = new JsonObject();
                response.addProperty("dateTime", dateTimeString);
                response.addProperty("fileName", fileName);
                response.addProperty("numberQuestions", Integer.parseInt(numberQuestionsString));
                response.addProperty("field", field);
                responseLogs.add(response);
            }

            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(responseLogs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
