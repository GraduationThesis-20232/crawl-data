package lawlaboratory.models.documents;

import java.util.ArrayList;

public class Article {
//    private int chapter_id;
    private String name;
    private ArrayList<String> content;

    public Article(String name, ArrayList<String> content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public int getChapter_id() {
//        return chapter_id;
//    }
//
//    public void setChapter_id(int chapter_id) {
//        this.chapter_id = chapter_id;
//    }


    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
