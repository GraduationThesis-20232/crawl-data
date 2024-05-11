package crawler.questions;

import crawler.BaseWebCrawler;
import database.questions.SaveQuestion;
import lawlaboratory.models.questions.Question;
import lawlaboratory.models.questions.Quote;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class QuestionsCrawler extends BaseWebCrawler {
    private HashMap<String, String> url_date = new HashMap<>();
    private HashMap<String, String> url_field = new HashMap<>();

    @Override
    public boolean connect(){
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
        ArrayList<String> allUrlCodes = new ArrayList<>();
        allUrlCodes.add("https://thuvienphapluat.vn/hoi-dap-phap-luat/tai-nguyen-moi-truong");      //New model

        for (int i = 0; i < allUrlCodes.size(); i++) {
            setUrl(allUrlCodes.get(i));
            if (!connect()){
                System.out.println("Kết nối thất bại");
                System.exit(0);
            }
            getDataAllQuestion(getAllUrlCodes());
        }
    }

    public void getDataAllQuestion(ArrayList<String> allUrlCodes) throws IOException {
        for (String url : allUrlCodes) {
            try {
                Connection connection = Jsoup.connect(url).maxBodySize(0).followRedirects(false);
                Document document = connection.get();
                Question question = new Question();
                question.setDate_answer(url_date.get(url));
                question.setField(url_field.get(url));

                if (document.getElementById("accordionMucLuc") != null){
                    getDataNewTemplate(document, question);
                }else if (document.getElementById("news-detail") != null){
                    getDataOldTemplate(document, question);
                }else {
                    System.out.println("Die " + url);
                }
            }catch (HttpStatusException e) {
                int statusCode = e.getStatusCode();
                System.out.println("HTTP error fetching URL. Status=" + statusCode + ", URL=" + url);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public ArrayList<String> getAllUrlCodes() throws IOException, ParseException {
        ArrayList<String> allUrlCodes = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm | dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Document document = getDoc();

        Element nextPage = document.select("ul.pagination > li.page-item > a.page-link").last();
        String lastPageUrl = nextPage.attr("href");

        String[] parts = lastPageUrl.split("=");
        String lastPart = parts[parts.length - 1];

        int numberPage = 0;
        try {
            numberPage = Integer.parseInt(lastPart);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format");
        }

        url_date.clear(); url_field.clear();
        for (int i = 1; i <= numberPage; i++) {
            String nextPageUrl = getUrl() + "?page=" + i;

            Connection connection = Jsoup.connect(nextPageUrl);
            document = connection.get();

            Elements articles  = document.select("section > article");
            for (Element a : articles) {
                allUrlCodes.add(a.select("a").attr("href"));
                Date date = inputFormat.parse(a.select(".sub-time").text());
                url_date.put(a.select("a").attr("href"), outputFormat.format(date));
                url_field.put(a.select("a").attr("href"), a.select("div.keyword > a").text());
            }
        }

        return allUrlCodes;
    }

    public void getDataNewTemplate(Document document, Question question){
        Element sectionContent = document.getElementById("news-content");

        Element current = sectionContent.getElementsByTag("h2").first();
        Pattern pattern = Pattern.compile("\\.{3,}");

        Quote quote = new Quote();
        ArrayList<String> contentQuotes = new ArrayList<>();
        ArrayList<String> conclusion = new ArrayList<>();
        do {
            if (current.is("h2")){
                question.setTitle(current.text());
                question.setReference(current.nextElementSibling().text());
            }

            if (current.is("blockquote") && !pattern.matcher(current.text()).find()){
                if (!current.select("strong").isEmpty()){
                    quote.setName(current.text());
                }else {
                    contentQuotes.add(current.text());
                }
            }

            if (current.nextElementSibling() != null && current.is("p") && !current.nextElementSibling().is("blockquote")){
                if (current.text() != null){
                    conclusion.add(current.text());
                }
            }

            if (current.nextElementSibling() == null || current.nextElementSibling().is("h2")){
                quote.setContent(contentQuotes);
                question.setQuote(quote);
                question.setConclusion(conclusion);

                SaveQuestion.getInstance().save(question, "temp");

                contentQuotes.clear();
                conclusion.clear();
            }

            current = current.nextElementSibling();
        }while (current != null);

    }

    public void getDataOldTemplate(Document document, Question question){
        String questionTitle = document.getElementById("news-detail").firstElementChild().text();

        question.setTitle(questionTitle);

        Elements sectionContent = document.getElementById("news-content").children();

        ArrayList<String> conclusion = new ArrayList<>();
        for (Element e : sectionContent) {
            if (e.is("strong")){
                question.setDescription(e.text());
            }
            if (e.is("p")){
                conclusion.add(e.text());
            }
        }
        question.setConclusion(conclusion);
        SaveQuestion.getInstance().save(question, "temp");
    }

    public static void main(String[] args) throws IOException, ParseException {
        QuestionsCrawler questionsCrawler = new QuestionsCrawler();
        questionsCrawler.start();

    }
}
