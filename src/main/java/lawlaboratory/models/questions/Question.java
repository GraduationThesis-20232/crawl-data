package lawlaboratory.models.questions;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Question {
    private ObjectId _id;
    private String title;
    private String description;
    private String date_answer;
    private String field;

    private String reference;
    private Quote quote;

    private ArrayList<String> conclusion;

    public Question() {
    }

    public ObjectId getId(ObjectId _id) {
        return _id;
    }
    public void setId(ObjectId id) {
        this._id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public ArrayList<String> getConclusion() {
        return conclusion;
    }

    public void setConclusion(ArrayList<String> conclusion) {
        this.conclusion = conclusion;
    }

    public String getDate_answer() {
        return date_answer;
    }

    public void setDate_answer(String date_answer) {
        this.date_answer = date_answer;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date_answer='" + date_answer + '\'' +
                ", field='" + field + '\'' +
                ", reference='" + reference + '\'' +
                ", quote=" + quote +
                ", conclusion=" + conclusion +
                '}';
    }


}
