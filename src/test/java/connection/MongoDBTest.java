package connection;

import database.documents.GetDocument;
import database.documents.UpdateDocument;
import lawlaboratory.models.documents.Article;
import lawlaboratory.models.documents.Chapter;
import lawlaboratory.models.documents.Law;
import lawlaboratory.models.documents.Part;
import org.bson.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MongoDBTest {
    public void printLaw(Law law) {

        for (Part part : law.getParts()) {
            System.out.println("Part: " + part.getName());

            for (Chapter chapter : part.getChapters()) {
                System.out.println("Chapter: " + chapter.getName());

                for (Article article : chapter.getArticles()) {
                    System.out.println("Article: " + article.getName());

                    for (String contentLine : article.getContent()) {
                        System.out.println(contentLine);
                    }
                }
            }
            System.out.println();
        }
    }

    public void updateDocument()
    {
        Law law = GetDocument.getInstance().getDocumentByIdentifier("101/2015/QH13", "test");

        law.setEffect_status(10);
        law.setSource_url("ABc");
        UpdateDocument.getInstance().update(law, "test");
    }

    public static void main(String[] args) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Date date = dateFormat.parse();

    }
}
