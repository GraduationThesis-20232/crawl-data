package lawlaboratory;

import lawlaboratory.controllers.CrawlerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class LawLaboratoryApplication {

    public static void main(String[] args) throws IOException, ParseException {
//        CrawlerController crawlerController = new CrawlerController();
//        crawlerController.crawlAllData();

        int senData = 1;

        SpringApplication.run(LawLaboratoryApplication.class, args);
    }

}
