package crawler.documents;

import crawler.BaseWebCrawler;
import database.documents.GetDocument;
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
import savetofile.LogCrawlManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConstitutionCrawler extends BaseWebCrawler implements DownloadFile {
    private int numberDocCrawled = 0;
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
        setUrl("https://luatvietnam.vn/van-ban-luat-viet-nam.html?OrderBy=0&keywords=&lFieldId=&EffectStatusId=0&DocTypeId=7&OrganId=0&page=1&pSize=20&ShowSapo=0");
        if (!connect()){
            System.out.println("Kết nối thất bại");
            System.exit(0);
        }
        getDataAllConstitution(getAllUrlConstitution());
        LogCrawlManager.getInstance().logDocumentCrawled("Hiến pháp", numberDocCrawled);
    }

    public void getDataAllConstitution(ArrayList<String> allUrlConstitutions) throws IOException, ParseException {
        try {
            for (String url: allUrlConstitutions) {
                Connection connection = Jsoup.connect(url);
                Document document = connection.get();

                Law law = new Law();
                Elements chaptersEle = document.select(".docitem-2");
                if (!chaptersEle.isEmpty()) {
                    law = constitutionsHasChapters(chaptersEle);
                } else continue;

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

                Law checkExits = GetDocument.getInstance().getDocumentByIdentifier(identifier, "constitution");

                if (checkExits != null && checkExits.getIdentifier().equals(identifier)){
                    break;
                } else {
                    downloadFile(document, identifier);
                    StoreDocument.getInstance().save(law, "constitution");
                    numberDocCrawled++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Law constitutionsHasChapters(Elements chaptersEle){
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

                Elements articlesEle = new Elements();
                for (Element ele : betweenEles) {
                    if (!ele.select(".docitem-5").isEmpty()){
                        if (ele.selectFirst("b") == null){
                            articlesName.add(ele.text());
                        } else articlesName.add(ele.selectFirst("b").text());
                        articlesEle.add(ele);
                    }
                }

                if (articlesEle.size() == 1){
                    ArrayList<String> tmp = new ArrayList<>();
                    Element temp = articlesEle.last().nextElementSibling();
                    do {
                        tmp.add(temp.text());
                        temp = temp.nextElementSibling();
                    }while (temp.is(".docitem-11, .docitem-12"));
                    articlesContent.add(tmp);
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
                        contentTemp.add(articlesEle.get(k).ownText());
                    }

                    articlesContent.add(contentTemp);

                    if (k == (articlesEle.size()-2)){
                        ArrayList<String> tmp = new ArrayList<>();
                        Element temp = articlesEle.last().nextElementSibling();

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


    public ArrayList<String> getAllUrlConstitution() throws IOException {
        ArrayList<String> allUrlConstitutions = new ArrayList<>();
        Document document = getDoc();

        do {
            Elements articles  = document.select(".doc-title > a");
            for (Element e: articles) {
                String url = "https://luatvietnam.vn" + e.attr("href");
                allUrlConstitutions.add(url);
            }

            Element nextPage = document.select("div.pagination > div.pag-right > a").last();

            if ( nextPage == null || !nextPage.text().equals("»")) break;

            String nextPageUrl = nextPage.attr("href");
            Connection connection = Jsoup.connect(nextPageUrl);
            document = connection.get();

        }while (true);

        return allUrlConstitutions;
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
