package lawlaboratory.models.questions;

import java.util.ArrayList;

public class Quote {
    private String name;
    private ArrayList<String> content;

    public Quote() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "name='" + name + '\'' +
                ", content=" + content +
                '}';
    }
}
