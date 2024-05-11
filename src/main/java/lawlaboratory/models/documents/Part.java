package lawlaboratory.models.documents;

import lawlaboratory.models.documents.Chapter;

import java.util.ArrayList;

public class Part {
//    private int law_id;
    private String name;
    private ArrayList<Chapter> chapters;

    public Part(){}

    public Part(String name, ArrayList<Chapter> chapters) {
        this.name = name;
        this.chapters = chapters;
    }

//    public int getLaw_id() {
//        return law_id;
//    }
//
//    public void setLaw_id(int law_id) {
//        this.law_id = law_id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
}
