package lawlaboratory.controllers;

import database.documents.GetDocument;
import lawlaboratory.models.documents.Law;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model) throws IOException {
        Map<String, Law> codesMap = GetDocument.getInstance().getListData("codes");
        Map<String, Law> constitutionsMap = GetDocument.getInstance().getListData("constitution");
        Map<String, Law> lawsMap = GetDocument.getInstance().getListData("laws");

        model.addAttribute("codesMap", codesMap);
        model.addAttribute("lawsMap", lawsMap);
        model.addAttribute("constitutionsMap", constitutionsMap);

        return "index";
    }

    @GetMapping("/codes/{id}")
    public String getCode(@PathVariable("id") String id, Model model) {
        Law lawValue = GetDocument.getInstance().getAData(id, "codes");

        if (lawValue.getParts() != null && !lawValue.getParts().isEmpty()){
            model.addAttribute("parts", lawValue.getParts());
            model.addAttribute("name", lawValue.getName());

            return "structure/parts";
        } else if (lawValue.getChapters() != null && !lawValue.getChapters().isEmpty()) {
            model.addAttribute("chapters", lawValue.getChapters());
            model.addAttribute("name", lawValue.getName());

            return "structure/chapters";
        } else if (lawValue.getArticles() != null && !lawValue.getArticles().isEmpty()) {
            model.addAttribute("articles", lawValue.getArticles());
            model.addAttribute("name", lawValue.getName());

            return "structure/articles";
        } else return "error";

    }

    @GetMapping("/laws/{id}")
    public String getLaw(@PathVariable("id") String id, Model model) {
        Law lawValue = GetDocument.getInstance().getAData(id, "laws");

        if (lawValue.getParts() != null && !lawValue.getParts().isEmpty()){
            model.addAttribute("parts", lawValue.getParts());
            model.addAttribute("name", lawValue.getName());

            return "structure/parts";
        } else if (lawValue.getChapters() != null && !lawValue.getChapters().isEmpty()) {
            model.addAttribute("chapters", lawValue.getChapters());
            model.addAttribute("name", lawValue.getName());

            return "structure/chapters";
        } else if (lawValue.getArticles() != null && !lawValue.getArticles().isEmpty()) {
            model.addAttribute("articles", lawValue.getArticles());
            model.addAttribute("name", lawValue.getName());

            return "structure/articles";
        } else return "error";
    }

    @GetMapping("/constitution/{id}")
    public String getConstituion(@PathVariable("id") String id, Model model) {
        Law lawValue = GetDocument.getInstance().getAData(id, "constitution");

        if (lawValue.getParts() != null && !lawValue.getParts().isEmpty()){
            model.addAttribute("parts", lawValue.getParts());
            model.addAttribute("name", lawValue.getName());

            return "structure/parts";
        } else if (lawValue.getChapters() != null && !lawValue.getChapters().isEmpty()) {
            model.addAttribute("chapters", lawValue.getChapters());
            model.addAttribute("name", lawValue.getName());

            return "structure/chapters";
        } else if (lawValue.getArticles() != null && !lawValue.getArticles().isEmpty()) {
            model.addAttribute("articles", lawValue.getArticles());
            model.addAttribute("name", lawValue.getName());

            return "structure/articles";
        } else return "error";
    }
}
