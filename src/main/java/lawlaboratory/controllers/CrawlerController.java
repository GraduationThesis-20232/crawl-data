package lawlaboratory.controllers;

import crawler.documents.AttributeIntegration;
import crawler.documents.CodesCrawler;
import crawler.documents.ConstitutionCrawler;
import crawler.documents.LawCrawler;
import crawler.questions.QuestionsCrawler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
public class CrawlerController {
    public static void main(String[] args) throws IOException, ParseException {
//        CodesCrawler codesCrawler = new CodesCrawler();
//        codesCrawler.start();
//
//        LawCrawler lawCrawler = new LawCrawler();
//        lawCrawler.start();

        ConstitutionCrawler constitutionCrawler = new ConstitutionCrawler();
        constitutionCrawler.start();

//        QuestionsCrawler questionsCrawler = new QuestionsCrawler();
//        questionsCrawler.start();

//        AttributeIntegration a = new AttributeIntegration();
//        a.start();
    }

    @PostMapping("/crawl/laws")
    public ResponseEntity<String> crawlAllLaws() throws IOException, ParseException {
        try {
        CodesCrawler codesCrawler = new CodesCrawler();
        codesCrawler.start();
//
//        LawCrawler lawCrawler = new LawCrawler();
//        lawCrawler.start();

        ConstitutionCrawler constitutionCrawler = new ConstitutionCrawler();
        constitutionCrawler.start();

//        AttributeIntegration a = new AttributeIntegration();
//        a.start();

            System.out.println("console: All laws crawled success!");
            return ResponseEntity.ok("api: All laws crawled success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/crawl/questions")
    public ResponseEntity<String> crawlAllQuestions() throws IOException, ParseException {
        try {
//        QuestionsCrawler questionsCrawler = new QuestionsCrawler();
//        questionsCrawler.start();

            System.out.println("console: All questions crawled success!");
            return ResponseEntity.ok("api: All questions crawled success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
