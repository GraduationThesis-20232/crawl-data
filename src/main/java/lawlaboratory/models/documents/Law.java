package lawlaboratory.models.documents;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Law {
    private ObjectId _id;
    private String name;
    private String identifier;      // Mã luật
    private String legislation;     // Loại văn bản
    private String effective_area;  // Phạm vi, lĩnh vực ảnh hưởng
    private String source_collection;       // Nguồn thu thập
    private String ministries;       // Ngành
    private String field;       // Lĩnh vực
    private String issuing_body;    // Cơ quan ban hành
    private String chairwoman;          // Chức danh
    private String signer;             // Người kí
    private String issued_date;       // Ngày ban hành
    private String effective_date;    // Ngày có hiệu lực
    private String gazette_date;       // Ngày đăng công báo

    private int effect_status;      // Hiệu lực
    private String source_url;             // Đường dẫn tham khảo
    private String reason_expiration;       // Lí do hết hạn
    private String expiry_date;         // Ngày hết hiệu lực

    private ArrayList<Part> parts;
    private ArrayList<Chapter> chapters;
    private ArrayList<Article> articles;
    public Law(){}

    public ArrayList<Part> getParts() {
        return parts;
    }

    public void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getMinistries() {
        return ministries;
    }

    public void setMinistries(String ministries) {
        this.ministries = ministries;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public String getIssued_date() {
        return issued_date;
    }

    public void setIssued_date(String issued_date) {
        this.issued_date = issued_date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIssuing_body() {
        return issuing_body;
    }

    public void setIssuing_body(String issuing_body) {
        this.issuing_body = issuing_body;
    }

    public String getLegislation() {
        return legislation;
    }

    public void setLegislation(String legislation) {
        this.legislation = legislation;
    }

    public String getEffective_area() {
        return effective_area;
    }

    public void setEffective_area(String effective_area) {
        this.effective_area = effective_area;
    }

    public int getEffect_status() {
        return effect_status;
    }

    public void setEffect_status(int effect_status) {
        this.effect_status = effect_status;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(String effective_date) {
        this.effective_date = effective_date;
    }

    public String getSource_collection() {
        return source_collection;
    }

    public void setSource_collection(String source_collection) {
        this.source_collection = source_collection;
    }

    public String getChairwoman() {
        return chairwoman;
    }

    public void setChairwoman(String chairwoman) {
        this.chairwoman = chairwoman;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getGazette_date() {
        return gazette_date;
    }

    public void setGazette_date(String gazette_date) {
        this.gazette_date = gazette_date;
    }

    public String getReason_expiration() {
        return reason_expiration;
    }

    public void setReason_expiration(String reason_expiration) {
        this.reason_expiration = reason_expiration;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    @Override
    public String toString() {
        return "Law{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", legislation='" + legislation + '\'' +
                ", effective_area='" + effective_area + '\'' +
                ", source_collection='" + source_collection + '\'' +
                ", ministries='" + ministries + '\'' +
                ", field='" + field + '\'' +
                ", issuing_body='" + issuing_body + '\'' +
                ", chairwoman='" + chairwoman + '\'' +
                ", signer='" + signer + '\'' +
                ", issued_date='" + issued_date + '\'' +
                ", effective_date='" + effective_date + '\'' +
                ", gazette_date='" + gazette_date + '\'' +
                ", effect_status=" + effect_status +
                ", source_url='" + source_url + '\'' +
                ", reason_expiration='" + reason_expiration + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                '}';
    }

    public String printContent() {
        return "Law{" +
                "name=" + name +
                ", articles=" + articles +
                ", parts=" + parts +
                ", chapters=" + chapters +
                '}';
    }
}
