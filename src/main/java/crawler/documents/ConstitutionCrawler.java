package crawler.documents;

import crawler.BaseWebCrawler;
import database.documents.StoreDocument;
import lawlaboratory.models.documents.Article;
import lawlaboratory.models.documents.Chapter;
import lawlaboratory.models.documents.Law;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import savetofile.DownloadFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConstitutionCrawler extends BaseWebCrawler implements DownloadFile {
    @Override
    public boolean connect() {
        File html = new File("src\\main\\resources\\data\\constitution\\Hienphapnam2013.html");

        try {
            Document doc = Jsoup.parse(html, "UTF-8");

            setDoc(doc);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void start() throws IOException, ParseException {
        if (!connect()){
            System.out.println("Kết nối thất bại");
            System.exit(0);
        }
        getDataAllConstitution();
    }

    public Law constitutionHasChapter(){
        Elements tmpChapter = getDoc().select("p[align=center]");
        Elements chaptersEle = new Elements();
        for (Element e : tmpChapter) {
            if (e.text().contains("CHƯƠNG")){
                chaptersEle.add(e);
            }
        }

        ArrayList<Chapter> chaptersObj = new ArrayList<>();
        ArrayList<String> chaptersName = new ArrayList<>();

        for (int i = 0; i < tmpChapter.size()-1; i++) {
            String name = tmpChapter.get(i).text();
            if (name.contains("CHƯƠNG")){
                chaptersName.add(name + " "+ tmpChapter.get(i+1).text());
            }
        }

        for (int j = 0; j < chaptersEle.size()-1; j++) {
            ArrayList<Article> articlesObj =  new ArrayList<>();

            Element startEle = chaptersEle.get(j);
            Element endEle = chaptersEle.get(j+1);
            Elements betweenEles = new Elements();
            Element currentEle = startEle.nextElementSibling().nextElementSibling();

            while (currentEle != null && !currentEle.equals(endEle)){
                betweenEles.add(currentEle);
                currentEle = currentEle.nextElementSibling();
            }

            ArrayList<String> articlesName = new ArrayList<>();
            ArrayList<ArrayList<String>> articlesContent = new ArrayList<>();

            Elements articlesEle = new Elements();
            for (Element ele : betweenEles) {
                if (!ele.select("b").isEmpty() && !ele.select("b").text().isEmpty()){
                    articlesName.add(ele.text());
                    articlesEle.add(ele);
                }
            }

            for (int k = 0; k < articlesEle.size()-1; k++) {
                Element sEle = articlesEle.get(k);
                Element eEle = articlesEle.get(k+1);
                Element cEle = sEle.nextElementSibling();
                Elements bEle = new Elements();

                while (cEle != null && !cEle.equals(eEle)){
                    bEle.add(cEle);
                    cEle = cEle.nextElementSibling();
                }

                ArrayList<String> contentTemp = new ArrayList<>();
                for (Element ln : bEle) {
                    contentTemp.add(ln.text());
                }

                if (contentTemp.isEmpty()){
                    contentTemp.add(articlesEle.get(k).text());
                }

                articlesContent.add(contentTemp);

                if (k == (articlesEle.size()-2)){
                    ArrayList<String> tmp = new ArrayList<>();
                    Element temp = articlesEle.last().nextElementSibling();

                    do {
                        tmp.add(temp.text());
                        temp = temp.nextElementSibling();
                    }while (!temp.text().contains("CHƯƠNG"));
                    articlesContent.add(tmp);
                }
            }

            for (int k = 0; k < articlesName.size(); k++) {
                articlesObj.add(new Article(articlesName.get(k), articlesContent.get(k)));
            }

            chaptersObj.add(new Chapter(chaptersName.get(j), articlesObj));

            if ((chaptersEle.size()-2) == j){
                Element temp = chaptersEle.last().nextElementSibling();
                String articleName = "";
                ArrayList<String> articleContent = new ArrayList<>();
                ArrayList<Article> tmp = new ArrayList<>();
                do {
                    temp = temp.nextElementSibling();
                    if (!temp.select("b").isEmpty() && !temp.select("b").text().isEmpty()){
                        articleName = temp.text();
                    }else articleContent.add(temp.text());

                    if (temp.nextElementSibling() != null && (temp.nextElementSibling().text().isEmpty() || temp.nextElementSibling().selectFirst("b") != null)){
                        if (articleContent.isEmpty()){
                            articleContent.add(temp.ownText());
                        }
                        tmp.add(new Article(articleName, articleContent));
                        articleName = "";
                        articleContent = new ArrayList<>();
                    }

                }while (temp.nextElementSibling() != null && !temp.nextElementSibling().text().isEmpty());

                chaptersObj.add(new Chapter(chaptersName.get(chaptersEle.size()-1), tmp));
            }
        }

        Law law = new Law();
        law.setChapters(chaptersObj);
        return law;
    }

    public void getDataAllConstitution() throws IOException, ParseException {
        String url = "https://luatvietnam.vn/tu-phap/hien-phap-18-2013-l-ctn-quoc-hoi-83320-d1.html";
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();

        Law law = constitutionHasChapter();
        String nameLaw = document.select("div.the-document-entry > div.the-document-summary").text();
        law.setName(nameLaw);
        law.setSource_url(url);
        law.setEffect_status(1);

        Elements tableInfo = document.select("#tomtat > div >  div.div-table > table > tbody");

        law.setIssuing_body(tableInfo.get(0).select("tr").get(0).select("td").get(1).text());
        String identifier = tableInfo.get(0).select("tr").get(1).select("td").get(1).text();
        law.setIdentifier(identifier);
        law.setLegislation(tableInfo.get(0).select("tr").get(2).select("td").get(1).text());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(tableInfo.get(0).select("tr").get(3).select("td").get(1).text());
        law.setIssued_date(outputFormat.format(date));

        law.setMinistries(tableInfo.get(0).select("tr").get(5).select("td").get(1).text());

        downloadFile(document, identifier);
        StoreDocument.getInstance().save(law, "constitution");
    }

    public void downloadFile(Document document, String identifier)
    {
        try {
            Element download = document.select("div#taive div.vn-doc div.section-content").first();
            Elements links = download.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                String title = link.attr("title");

                if (title.contains("Word")) {
                    download(href, identifier.replace("/", "_") + ".docx");
                } else if (title.contains("PDF")) {
                    download(href, identifier.replace("/", "_") + ".pdf");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        ConstitutionCrawler crawler = new ConstitutionCrawler();
        crawler.start();
    }
}
