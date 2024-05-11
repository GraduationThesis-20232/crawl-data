package lawlaboratory.models.documents;

import lawlaboratory.models.documents.Article;

import java.util.ArrayList;

public class Chapter {
//    private int part_id;
    private String name;
    private ArrayList<Article> articles;

//    public int getPart_id() {
//        return part_id;
//    }
//
//    public void setPart_id(int part_id) {
//        this.part_id = part_id;
//    }

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

    public Chapter(String name, ArrayList<Article> articles) {
        this.name = name;
        this.articles = articles;
    }
    public Chapter(){};

    @Override
    public String toString() {
        return "Chapter{" +
                "name='" + name + '\'' +
                ", articles=" + articles +
                '}';
    }
}
