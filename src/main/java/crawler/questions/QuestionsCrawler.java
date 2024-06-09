package crawler.questions;

import crawler.BaseWebCrawler;
import database.questions.GetQuestion;
import lawlaboratory.models.questions.Question;
import lawlaboratory.models.questions.Quote;
import org.javatuples.Triplet;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import savetofile.LogCrawlManager;
import savetofile.QuestionJSONWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class QuestionsCrawler extends BaseWebCrawler {
    private HashMap<String, String> url_date = new HashMap<>();
//    private HashMap<String, String> url_field = new HashMap<>();
    private String filePath = "";
    private final Date dateAnswerNearest = new SimpleDateFormat("yyyy-MM-dd").parse(GetQuestion.getInstance().getNearestDateAnswer("questions"));
    private int numberQuestionCrawled = 0;
    private String field = "";

    public QuestionsCrawler() throws ParseException {
    }

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
        String prefixUrl = "https://thuvienphapluat.vn/hoi-dap-phap-luat/";
        ArrayList<Triplet<String, String, String>> url_file_field = new ArrayList<>();

        url_file_field.add(new Triplet<>( prefixUrl + "tien-te-ngan-hang", "tiente_nganhang.json", "Tiền tệ - Ngân hàng"));
        url_file_field.add(new Triplet<>( prefixUrl + "quyen-dan-su", "quyendansu.json", "Quyền dân sự"));
        url_file_field.add(new Triplet<>( prefixUrl + "chung-khoan", "chungkhoan.json", "Chứng khoán"));
        url_file_field.add(new Triplet<>( prefixUrl + "so-huu-tri-tue", "sohuutritue.json", "Sở hữu trí tuệ"));
        url_file_field.add(new Triplet<>( prefixUrl + "tai-chinh-nha-nuoc", "taichinh_nhanuoc.json", "Tài chính nhà nước"));
        url_file_field.add(new Triplet<>( prefixUrl + "thu-tuc-to-tung", "thutuc_totung.json", "Thủ tục tố tụng"));
        url_file_field.add(new Triplet<>( prefixUrl + "the-thao-y-te", "thethao_yte.json", "Thể thao - Y tế"));
        url_file_field.add(new Triplet<>( prefixUrl + "giao-thong-van-tai", "giaothong_vantai.json", "Giao thông vận tải"));
        url_file_field.add(new Triplet<>( prefixUrl + "xuat-nhap-khau", "xuatnhapkhau.json", "Xuất nhập khẩu"));

        url_file_field.add(new Triplet<>( prefixUrl + "doanh-nghiep", "doanhnghiep.json", "Doanh nghiệp"));
        url_file_field.add(new Triplet<>( prefixUrl + "lao-dong-tien-luong", "laodong_tienluong.json", "Lao động - Tiền lương"));
        url_file_field.add(new Triplet<>( prefixUrl + "bat-dong-san", "batdongsan.json", "Bất động sản"));
        url_file_field.add(new Triplet<>( prefixUrl + "vi-pham-hanh-chinh", "vipham_hanhchinh.json", "Vi phạm hành chính"));
        url_file_field.add(new Triplet<>( prefixUrl + "bao-hiem", "baohiem.json", "Bảo hiểm"));
        url_file_field.add(new Triplet<>( prefixUrl + "van-hoa-xa-hoi", "vanhoa_xahoi.json", "Văn hóa - Xã hội"));
        url_file_field.add(new Triplet<>( prefixUrl + "thuong-mai", "thuongmai.json", "Thương mại"));
        url_file_field.add(new Triplet<>( prefixUrl + "trach-nhiem-hinh-su", "trachnhiem_hinhsu.json", "Trách nhiệm hình sự"));
        url_file_field.add(new Triplet<>( prefixUrl + "xay-dung-do-thi", "xaydung_dothi.json", "Xây dựng - Đô thị"));

        url_file_field.add(new Triplet<>( prefixUrl + "ke-toan-kiem-toan", "ketoan_kiemtoan.json", "Kế toán - Kiểm toán"));
        url_file_field.add(new Triplet<>( prefixUrl + "thue-phi-le-phi", "thuephi_lephi.json", "Thuế - Phí - Lệ phí"));
        url_file_field.add(new Triplet<>( prefixUrl + "dau-tu", "dautu.json", "Đầu tư"));
        url_file_field.add(new Triplet<>( prefixUrl + "dich-vu-phap-ly", "dichvu_phaply.json", "Dịch vụ pháp lý"));
        url_file_field.add(new Triplet<>( prefixUrl + "tai-nguyen-moi-truong", "tainguyen_moitruong.json", "Tài nguyên - Môi trường"));
        url_file_field.add(new Triplet<>( prefixUrl + "cong-nghe-thong-tin", "congnghe_thongtin.json", "Công nghệ thông tin"));
        url_file_field.add(new Triplet<>( prefixUrl + "giao-duc", "giaoduc.json", "Giáo dục"));
        url_file_field.add(new Triplet<>( prefixUrl + "bo-may-hanh-chinh", "bomay_hanhchinh.json", "Bộ máy hành chính"));
//        url_file_field.add(new Triplet<>( prefixUrl + "linh-vuc-khac", "linhvuckhac.json", "Lĩnh vực khác"));

        for (Triplet<String, String, String> uff : url_file_field) {
            this.numberQuestionCrawled = 0;
            setUrl(uff.getValue0());
            this.filePath = uff.getValue1();
            this.field = uff.getValue2();
            if (!connect()) {
                System.out.println("Kết nối thất bại");
                System.exit(0);
            }
            ArrayList<String> listAllUrl = removeDuplicates(getAllUrlCodes());
            getDataAllQuestion(listAllUrl);
            LogCrawlManager.getInstance().logQuestionsCrawled(this.filePath, this.numberQuestionCrawled, this.field);
        }
    }

    public void getDataAllQuestion(ArrayList<String> allUrlCodes) throws IOException {
        for (String url : allUrlCodes) {
            try {
                Connection connection = Jsoup.connect(url).maxBodySize(0).followRedirects(false);
                Document document = connection.get();
                Question question = new Question();
                question.setDate_answer(url_date.get(url));
                question.setSource_url(url);
//                question.setField(url_field.get(url));
                question.setField(this.field);

                if (document.getElementById("accordionMucLuc") != null){
                    getDataNewTemplate(document, question);
                }else if (document.getElementById("news-content") != null){
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

        url_date.clear();
//        url_field.clear();
        for (int i = 1; i <= numberPage; i++) {
            System.out.println(i);
            if(i == 10) break;
            String nextPageUrl = getUrl() + "?page=" + i;

            int maxRetries = 5;
            int retries = 0;
            boolean success = false;

            while (retries < maxRetries && !success) {
                try {
                    Connection connection = Jsoup.connect(nextPageUrl)
                            .userAgent(HttpConnection.DEFAULT_UA)
                            .timeout(5000);
                    document = connection.get();

                    success = true;
                } catch (java.net.SocketTimeoutException e) {
                    retries++;
                    System.out.println("Read timed out, retrying... (" + retries + "/" + maxRetries + ")");
                    if (retries >= maxRetries) {
                        System.out.println("Failed after " + maxRetries + " attempts");
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }

            if (success) {
                Elements articles  = document.select("section > article");
                for (Element a : articles) {
                    Date date = inputFormat.parse(a.select(".sub-time").text());
                    if (date.before(dateAnswerNearest)){
                        continue;
                    }

                    allUrlCodes.add(a.select("a").attr("href"));
                    url_date.put(a.select("a").attr("href"), outputFormat.format(date));
//                    url_field.put(a.select("a").attr("href"), a.select("div.keyword > a").text());
                }
            } else {
                System.out.println("Unable to retrieve the document.");
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

//                SaveQuestion.getInstance().save(question, "temp");
                QuestionJSONWriter.getInstance().saveToJSON(question, filePath);
                this.numberQuestionCrawled++;

                contentQuotes.clear();
                conclusion.clear();
            }

            current = current.nextElementSibling();
        }while (current != null);

    }

    public void getDataOldTemplate(Document document, Question question){
        String questionTitle = document.select("header > h1.h3.fw-bold.title").first().text();

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

//        SaveQuestion.getInstance().save(question, "temp");
        QuestionJSONWriter.getInstance().saveToJSON(question, filePath);
        this.numberQuestionCrawled++;
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        HashSet<T> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

    public static void main(String[] args) throws IOException, ParseException {
        QuestionsCrawler questionsCrawler = new QuestionsCrawler();
        questionsCrawler.start();
    }
}
