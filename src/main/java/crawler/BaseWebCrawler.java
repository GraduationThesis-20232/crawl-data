package crawler;

import lawlaboratory.models.documents.Law;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;

public abstract class BaseWebCrawler {
    private String url;
    private Document doc = null;

    public BaseWebCrawler() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public abstract boolean connect();

    public abstract void start() throws IOException, ParseException;
}
