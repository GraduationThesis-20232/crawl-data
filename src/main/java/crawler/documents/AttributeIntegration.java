package crawler.documents;

import crawler.BaseWebCrawler;
import database.documents.GetDocument;
import database.documents.UpdateDocument;
import lawlaboratory.models.documents.Law;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AttributeIntegration extends BaseWebCrawler {
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
        setUrl("https://vbpl.vn/TW/Pages/vanban.aspx?idLoaiVanBan=17&dvid=13&Page=1");
        if (!connect()){
            System.out.println("Kết nối thất bại");
            System.exit(0);
        }
        getAllAttribute(getAllUrlLaws());
    }

    private void getAllAttribute(ArrayList<String> allUrlLaws) {
        try {
            for (String url: allUrlLaws) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                Connection connection = Jsoup.connect(url);
                Document dom = connection.get();

                Element vbInfo = dom.selectFirst("div.vbInfo");
                Element vbProperties = dom.selectFirst("div.vbProperties");

                String identifier = "";
                Elements rowsTable = vbProperties.select("table > tbody > tr");
                for (Element e: rowsTable) {
                    if (e.firstElementChild().text().equals("Số ký hiệu")){
                        identifier = e.firstElementChild().nextElementSibling().text();
                    }
                }

                Law law = GetDocument.getInstance().getDocumentByIdentifier(identifier, "laws");
                if (law == null) {
                    continue;
                }

                Elements liTags = vbInfo.select("ul > li");
                for (Element e: liTags) {
                    if (e.firstElementChild().text().equals("Hiệu lực:")) {
                        String tmp = e.text().replace("Hiệu lực: ", "");
                        if (tmp.equals("Còn hiệu lực")) {
                            law.setEffect_status(1);
                        } else if (tmp.equals("Hết hiệu lực toàn bộ")) {
                            law.setEffect_status(0);
                        } else if (tmp.equals("Hết hiệu lực một phần")) {
                            law.setEffect_status(2);
                        }
                    }

                    if (e.firstElementChild().text().equals("Ngày có hiệu lực:")) {
                        String tmp = e.text().replace("Ngày có hiệu lực: ", "");
                        Date date = dateFormat.parse(tmp);
                        law.setEffective_date(outputFormat.format(date));
                    }
                }

                for (Element e: rowsTable) {
                    if (e.firstElementChild().text().equals("Số ký hiệu")){
                        Elements children = e.children();
                        for (Element child: children) {
                            if (child.text().equals("Ngày ban hành")) {
                                Date date = dateFormat.parse(child.nextElementSibling().text());
                                law.setIssued_date(outputFormat.format(date));
                            }
                        }
                    }

                    if (e.firstElementChild().text().equals("Loại văn bản")){
                        Elements children = e.children();
                        for (Element child: children) {
                            if (child.text().equals("Loại văn bản")) {
                                law.setLegislation(child.nextElementSibling().text());
                            }
                        }
                    }

                    if (e.firstElementChild().text().equals("Nguồn thu thập")){
                        Elements children = e.children();
                        for (Element child: children) {
                            if (child.text().equals("Nguồn thu thập")) {
                                law.setSource_collection(child.nextElementSibling().text());
                            }

                            if (child.text().equals("Ngày đăng công báo") && child.nextElementSibling().text().length() == 10) {
                                Date date = dateFormat.parse(child.nextElementSibling().text());
                                law.setGazette_date(outputFormat.format(date));
                            }
                        }
                    }

                    if (e.firstElementChild().text().equals("Ngành")){
                        Elements children = e.children();
                        for (Element child: children) {
                            if (child.text().equals("Ngành")) {
                                law.setMinistries(child.nextElementSibling().text());
                            }

                            if (child.text().equals("Lĩnh vực")) {
                                law.setField(child.nextElementSibling().text());
                            }
                        }
                    }

                    if (e.firstElementChild().text().equals("Cơ quan ban hành/ Chức danh / Người ký")){
                        Elements children = e.children();
                        for (int i = 0; i < children.size(); i++) {
                            if (i==1) {
                                law.setIssuing_body(children.get(i).text());
                            }

                            if (i==2) {
                                law.setChairwoman(children.get(i).text());
                            }

                            if (i==3) {
                                law.setSigner(children.get(i).text());
                            }
                        }
                    }

                    if (e.firstElementChild().text().equals("Phạm vi")) {
                        law.setEffective_area(e.firstElementChild().nextElementSibling().text());
                    }

                    if (e.firstElementChild().text().equals("Lí do hết hiệu lực")) {
                        Elements children = e.children();
                        for (Element child: children) {
                            if (child.text().equals("Lí do hết hiệu lực")) {
                                law.setReason_expiration(child.nextElementSibling().text());
                            }

                            if (child.text().equals("Ngày hết hiệu lực") && child.nextElementSibling().text().length()==10) {
                                Date date = dateFormat.parse(child.nextElementSibling().text());
                                law.setExpiry_date(outputFormat.format(date));
                            }
                        }
                    }
                }
                String sourceUrl = "https://vbpl.vn" + dom.select("div.header > ul > li").first().select("a").attr("href");
                law.setSource_url(sourceUrl);
                UpdateDocument.getInstance().update(law, "laws");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getAllUrlLaws() throws IOException {
        ArrayList<String> allUrlCodes = new ArrayList<>();
        try {
            Document document = getDoc();

            Element lastPage = document.select(".paging > a").last();

            int numberPage = 0;
            if (lastPage == null) {
                numberPage = 1;
            } else {
                numberPage = getNumberPages(lastPage.attr("href"));
            }

            for (int i = 1; i <= numberPage; i++) {
                String urlPage = "https://vbpl.vn/TW/Pages/vanban.aspx?idLoaiVanBan=17&dvid=13&Page=" + i;
                Connection connection = Jsoup.connect(urlPage);
                Document documentPage = connection.get();
                Elements allUrlPage =  documentPage.select("li.thuoctinh > a");

                for (Element e: allUrlPage) {
                    allUrlCodes.add("https://vbpl.vn/" + e.attr("href"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allUrlCodes;
    }

    private int getNumberPages(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            String[] queryParams = query.split("&");
            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("Page")) {
                    return Integer.parseInt(keyValue[1]);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) throws IOException, ParseException {
        AttributeIntegration integration = new AttributeIntegration();
        integration.start();
    }
}
