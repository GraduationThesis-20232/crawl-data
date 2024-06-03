package crawler.documents;

import crawler.BaseWebCrawler;
import savetofile.DownloadFile;
import database.documents.StoreDocument;
import lawlaboratory.models.documents.Article;
import lawlaboratory.models.documents.Chapter;
import lawlaboratory.models.documents.Law;
import lawlaboratory.models.documents.Part;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CodesCrawler extends BaseWebCrawler implements DownloadFile {

    public CodesCrawler() {
    }

    @Override
    public boolean connect() {
        Document document = null;
        try {
            document = Jsoup
                    .connect(getUrl())
                    .userAgent("Jsoup client")
                    .timeout(5000).get();
            setDoc(document);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void start() throws IOException, ParseException {
        setUrl("https://luatvietnam.vn/van-ban-luat-viet-nam.html?OrderBy=0&keywords=&lFieldId=&EffectStatusId=0&DocTypeId=58&OrganId=0&page=1&pSize=50&ShowSapo=0");
        if (!connect()){
            System.out.println("Kết nối thất bại");
            System.exit(0);
        }
        getDataAllCodes(getAllUrlCodes());
    }

    public ArrayList<String> getAllUrlCodes(){
        ArrayList<String> allUrlCodes = new ArrayList<String>();
        Document document = getDoc();

        Elements articles  = document.select(".doc-title > a");

        for (Element e: articles) {
            String url = "https://luatvietnam.vn" + e.attr("href");
//            if (url.equals("https://luatvietnam.vn/lao-dong/bo-luat-lao-dong-2012-71731-d1.html")) break;
            allUrlCodes.add(url);
        }

        return allUrlCodes;
    }

    public void getDataAllCodes(ArrayList<String> allUrlCodes) throws IOException, ParseException {
        try {
            for (String url : allUrlCodes) {

                Connection connection = Jsoup.connect(url);
                Document document = connection.get();

                Law law = new Law();
                Elements partsEle = document.select(".docitem-1");
                Elements chaptersEle = document.select(".docitem-2");

                if (!partsEle.isEmpty() && !chaptersEle.isEmpty()){
                    law = codesHasPart(partsEle);
                } else if(partsEle.isEmpty() && !chaptersEle.isEmpty()){
                    law = codesHasChapter(chaptersEle);
                } else {
                    continue;
                }

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
                StoreDocument.getInstance().save(law, "codes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Law codesHasPart(Elements partsEle){

        ArrayList<Part> partsObj = new ArrayList<>();
        ArrayList<String> partsName = new ArrayList<>();

        for (Element p : partsEle) {
            partsName.add(p.text());
        }

        try {
            for (int i = 0; i < partsEle.size()-1; i++) {
                ArrayList<Chapter> chaptersObj = new ArrayList<>();

                Element startElement = partsEle.get(i);
                Element endElement = partsEle.get(i+1);
                Elements betweenElements = new Elements();
                Element currentElement = startElement.nextElementSibling();

                while (currentElement != null && !currentElement.equals(endElement)){
                    betweenElements.add(currentElement);
                    currentElement = currentElement.nextElementSibling();
                }

                Elements chaptersEle = new Elements();
                ArrayList<String> chaptersName = new ArrayList<>();
                for (Element e : betweenElements ) {
                    if (!e.select(".docitem-2").isEmpty()){
                        chaptersEle.add(e);
                        chaptersName.add(e.text());
                    }
                }

                for (int j = 0; j < chaptersEle.size()-1; j++) {
                    ArrayList<Article> articlesObj = new ArrayList<>();

                    Element startEle = chaptersEle.get(j);
                    Element endEle = chaptersEle.get(j+1);
                    Elements betweenEles = new Elements();
                    Element currentEle = startEle.nextElementSibling();

                    while (currentEle != null && !currentEle.equals(endEle)){
                        betweenEles.add(currentEle);
                        currentEle = currentEle.nextElementSibling();
                    }

                    ArrayList<String> articlesName = new ArrayList<>();
                    ArrayList<ArrayList<String>> articlesContent = new ArrayList<>();

                    Elements contentArticle = new Elements();
                    for (Element ele : betweenEles) {
                        if (!ele.select(".docitem-5").isEmpty()){
                            articlesName.add(ele.selectFirst("b").text());
                            contentArticle.add(ele);
                        }
                    }

                    if (contentArticle.size() == 1){
                        ArrayList<String> tmp = new ArrayList<>();
                        Element temp = contentArticle.last().nextElementSibling();
                        do {
                            tmp.add(temp.text());
                            temp = temp.nextElementSibling();
                        }while (temp.is(".docitem-11, .docitem-12"));
                        articlesContent.add(tmp);
                    }

                    for (int k = 0; k < contentArticle.size()-1; k++) {
                        Element sEle = contentArticle.get(k);
                        Element eEle = contentArticle.get(k+1);
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
                            contentTemp.add(contentArticle.get(k).ownText());
                        }

                        articlesContent.add(contentTemp);

                        if (k == (contentArticle.size()-2)){
                            ArrayList<String> tmp = new ArrayList<>();
                            Element temp = contentArticle.last().nextElementSibling();

                            do {
                                tmp.add(temp.text());
                                temp = temp.nextElementSibling();
                            }while (temp.hasClass("docitem-11") || temp.hasClass("docitem-12"));

                            articlesContent.add(tmp);
                        }
                    }

                    for (int k = 0; k < articlesName.size(); k++) {
                        articlesObj.add(new Article(articlesName.get(k), articlesContent.get(k)));
                    }

                    chaptersObj.add(new Chapter(chaptersName.get(j), articlesObj));

                    if ((chaptersEle.size()-2) == j){
                        Element temp = chaptersEle.last();
                        String articleName = "";
                        ArrayList<String> articleContent = new ArrayList<>();
                        ArrayList<Article> tmp = new ArrayList<>();
                        do {
                            temp = temp.nextElementSibling();
                            if (temp.hasClass("docitem-5")){
                                articleName = temp.text();
                            }else articleContent.add(temp.text());

                            if (temp.nextElementSibling().is(".docitem-5, .docitem-1, .docitem-2")){
                                if (articleContent.isEmpty()){
                                    articleContent.add(temp.ownText());
                                }
                                tmp.add(new Article(articleName, articleContent));
                                articleName = "";
                                articleContent = new ArrayList<>();
                            }

                        }while (!temp.nextElementSibling().is(".docitem-1, .docitem-2"));

                        chaptersObj.add(new Chapter(chaptersName.get(chaptersEle.size()-1), tmp));
                    }
                }
                partsObj.add(new Part(partsName.get(i).toString(), chaptersObj));

                if ((partsEle.size()-2 == i)){
                    Element temp = partsEle.last();
                    String articleName = "";
                    ArrayList<String> articleContent = new ArrayList<>();
                    ArrayList<Article> tmp = new ArrayList<>();

                    do {
                        temp = temp.nextElementSibling();
                        if (temp.hasClass("docitem-5")){
                            articleName = temp.text();
                        }else articleContent.add(temp.text());

                        if (temp.nextElementSibling().is(".docitem-5, .docitem-9")){
                            if (articleContent.isEmpty()){
                                articleContent.add(temp.ownText());
                            }
                            tmp.add(new Article(articleName, articleContent));
                            articleName = "";
                            articleContent = new ArrayList<>();
                        }
                    }while (!temp.nextElementSibling().is(".docitem-9"));

                    ArrayList<Chapter> chapterTmp = new ArrayList<>();
                    chapterTmp.add(new Chapter("", tmp));
                    partsObj.add(new Part(partsName.get(partsEle.size()-1).toString(), chapterTmp));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Law law = new Law();
        law.setParts(partsObj);

        return law;
    }

    public Law codesHasChapter(Elements chaptersEle){
        ArrayList<Chapter> chaptersObj = new ArrayList<>();
        ArrayList<String> chaptersName = new ArrayList<>();

        for (Element e : chaptersEle) {
            chaptersName.add(e.text());
        }

        try {
            for (int j = 0; j < chaptersEle.size()-1; j++) {
                ArrayList<Article> articlesObj =  new ArrayList<>();

                Element startEle = chaptersEle.get(j);
                Element endEle = chaptersEle.get(j+1);
                Elements betweenEles = new Elements();
                Element currentEle = startEle.nextElementSibling();

                while (currentEle != null && !currentEle.equals(endEle)){
                    betweenEles.add(currentEle);
                    currentEle = currentEle.nextElementSibling();
                }

                ArrayList<String> articlesName = new ArrayList<>();
                ArrayList<ArrayList<String>> articlesContent = new ArrayList<>();

                Elements contentArticle = new Elements();
                for (Element ele : betweenEles) {
                    if (!ele.select(".docitem-5").isEmpty()){
                        articlesName.add(ele.selectFirst("b").text());
                        contentArticle.add(ele);
                    }
                }

                if (contentArticle.size() == 1){
                    ArrayList<String> tmp = new ArrayList<>();
                    Element temp = contentArticle.last().nextElementSibling();
                    do {
                        tmp.add(temp.text());
                        temp = temp.nextElementSibling();
                    }while (temp.is(".docitem-11, .docitem-12"));
                    articlesContent.add(tmp);
                }

                for (int k = 0; k < contentArticle.size()-1; k++) {
                    Element sEle = contentArticle.get(k);
                    Element eEle = contentArticle.get(k+1);
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
                        contentTemp.add(contentArticle.get(k).ownText());
                    }

                    articlesContent.add(contentTemp);

                    if (k == (contentArticle.size()-2)){
                        ArrayList<String> tmp = new ArrayList<>();
                        Element temp = contentArticle.last().nextElementSibling();

                        do {
                            tmp.add(temp.text());
                            temp = temp.nextElementSibling();
                        }while (temp.is(".docitem-11, .docitem-12"));

                        articlesContent.add(tmp);
                    }
                }

                for (int k = 0; k < articlesName.size(); k++) {
                    articlesObj.add(new Article(articlesName.get(k), articlesContent.get(k)));
                }

                chaptersObj.add(new Chapter(chaptersName.get(j), articlesObj));

                if ((chaptersEle.size()-2) == j){
                    Element temp = chaptersEle.last();
                    String articleName = "";
                    ArrayList<String> articleContent = new ArrayList<>();
                    ArrayList<Article> tmp = new ArrayList<>();
                    do {
                        temp = temp.nextElementSibling();
                        if (temp.hasClass("docitem-5")){
                            articleName = temp.text();
                        }else articleContent.add(temp.text());

                        if (temp.nextElementSibling() != null && temp.nextElementSibling().is(".docitem-5, .docitem-9")){
                            if (articleContent.isEmpty()){
                                articleContent.add(temp.ownText());
                            }
                            tmp.add(new Article(articleName, articleContent));
                            articleName = "";
                            articleContent = new ArrayList<>();
                        }

                    }while (temp.nextElementSibling() != null && !temp.nextElementSibling().is(".docitem-9"));

                    chaptersObj.add(new Chapter(chaptersName.get(chaptersEle.size()-1), tmp));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Law law = new Law();
        law.setChapters(chaptersObj);

        return law;
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
        CodesCrawler crawler = new CodesCrawler();
        crawler.start();
    }
}
