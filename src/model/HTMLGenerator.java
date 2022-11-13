package model;

import handlers.ArgumentsHandler;
import java.util.ArrayList;
import java.util.List;

import static consts.EmbeddedTypes.HREF;

public class HTMLGenerator {

    List<Rows> tableElements;

    //DEMO DATAS
    String burnedTitle = "L&amp;P Solutions";
    String burnedHref = "http://lpsolutions.hu";
    String burnedMyGithubHref = "https://github.com/DViktor-refs/HTMLGenerator";
    List<Embedded> embeddedContent = new ArrayList<>();

    public String getHtml() {
        ArgumentsHandler argumentsHandler = ArgumentsHandler.getInstance();
        tableElements = argumentsHandler.createTableElementsFromArgs();

        //DEMO embedded content
        embeddedContent.add(new Embedded(HREF, burnedMyGithubHref, "Megoldás"));

        HTMLGeneratorApi HTMLAPI =
                new HTMLGeneratorApi.HTMLGeneratorApiBuilder()
                        .startNewDocument()
                        .addNewHead("Teszt Feladat")
                        .addNewBodyStartMarker()
                        .addNewHeading("Teszt Feladat", "h1")
                        .addNewParagraph(null, embeddedContent)
                        .addNewParagraph("A feladat elkészítőjének adatai", null)
                        .addNewTable(1, tableElements)
                        .addNewHref(burnedTitle, burnedHref)
                        .addNewBodyEndMarker()
                        .close();

        return HTMLAPI.getComplettedHTMLcode().toString();
    }


}
