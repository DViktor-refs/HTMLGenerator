package model;

import handlers.ArgumentsHandler;
import java.util.*;
import java.util.stream.Collectors;
import static consts.FieldNames.*;

public class HTMLGeneratorApi {

    private StringBuilder sb = new StringBuilder();
    public StringBuilder getComplettedHTMLcode() {
        return sb;
    }

    public HTMLGeneratorApi(HTMLGeneratorApiBuilder builder) {
        this.sb = builder.sb;
    }

    public static class HTMLGeneratorApiBuilder {

        private final StringBuilder sb = new StringBuilder();
        private final List<String> otherArgumentsList = new ArrayList<>();
        private int columnPosition = 0;
        private int bodyPosition;
        private boolean isItaNewDocument = true;
        private boolean isHeadExistsMoreThanOneTime = false;
        private boolean isBodyExistsMoreThanOneTime = false;

        private ArgumentsHandler argumentsHandler = ArgumentsHandler.getInstance();

        public HTMLGeneratorApiBuilder startNewDocument() {

            if (isItaNewDocument) {
                generateOtherArgumentsList();
                createDocumentHeaderPart();
                isItaNewDocument = false;
            }
            else {
                throw new IllegalArgumentException("You can't use more than one times the startNewDocument method.");
            }
            return this;
        }

        public HTMLGeneratorApiBuilder addNewHead(String title) {
            if (!isHeadExistsMoreThanOneTime) {
                if (!isNoPrintHeadActivated()) {
                    moveColumnPosToRight();
                    createHeadPart(title);
                    isHeadExistsMoreThanOneTime = true;
                }
            }
            else {
                throw new IllegalStateException("Only one 'HEAD' part allowed.");
            }
            return this;
        }

        public HTMLGeneratorApiBuilder addNewHeading(String title, String type_h1_to_h6) {
            List<String> availableSizes = new ArrayList<>(Arrays.asList("h1","h2","h3","h4","h5","h6"));
            String type = type_h1_to_h6.toLowerCase(Locale.ROOT);

            if (availableSizes.contains(type)) {
                if (!otherArgumentsList.contains(NOPRINTHEADING)) {
                    createHeadingPart(type, title);
                }
            }
            else {
                throw new IllegalStateException("Invalid type error. Please use \"h1\" to \"h6\"");
            }
            return this;
        }

        public HTMLGeneratorApiBuilder addNewParagraph(String title, List<Embedded> embeddedContent) {

            if (!otherArgumentsList.contains(NOPRINTPARAGRAPHS)) {
                if (embeddedContent != null) {
                    for (Embedded embedded : embeddedContent) {
                        switch (embedded.getType()) {
                            case 1: //HREF
                                createParagraphPartIfEmbeddIsHref(embedded);
                                break;

                            case 2: //PARAGRAPH
                                createParagraphPartIfEmbeddedIsParagraph(embedded.getContent());
                                break;

                            case 3: //HEADING
                                createParagraphPartIfEmbeddedIsHeading(
                                        embedded.getContent(),
                                        embedded.getModifier());
                                break;
                        }
                    }
                }
                else {
                    createParagraphPart(title);
                }
            }

            return this;
        }

        public HTMLGeneratorApiBuilder addNewBodyStartMarker() {
            if (!isBodyExistsMoreThanOneTime) {
                bodyPosition = columnPosition;
                createBodyStartPart();
                moveColumnPosToRight();
                isBodyExistsMoreThanOneTime = true;
            }
            else {
                throw new IllegalStateException("Only one 'BODY' part allowed.");
            }

            return this;
        }

        public HTMLGeneratorApiBuilder addNewBodyEndMarker() {
            if (isBodyExistsMoreThanOneTime) {
                createBodyEndPart();
            }
            else {
                throw new IllegalStateException("You need to start a BODY element if you want to close one .");
            }

            return this;
        }

        public HTMLGeneratorApiBuilder addNewHref(String nameOfHref, String href) {

            if (!otherArgumentsList.contains(NOPRINTHREF)) {
                createHrefPart(nameOfHref, href);
            }

            return this;
        }

        public HTMLGeneratorApiBuilder addNewTable(int borderWidth, List<Rows> tableRows) {

            if (!otherArgumentsList.contains(NOPRINTTABLE)) {
                createTableBorderStartPart(borderWidth, columnPosition);
                moveColumnPosToRight();

                for (int i = 0; i < tableRows.size(); i++) {
                    if (!otherArgumentsList.contains(NOPRINTTR)) {
                        createTableTrStartPart(columnPosition);
                        if (!otherArgumentsList.contains(NOPRINTTD)) {
                            moveColumnPosToRight();
                            createTableTdPart(columnPosition, tableRows, i);
                            moveColumnPosToLeft();
                        }
                        createTableTrEndPart(columnPosition);
                    }
                }
                moveColumnPosToLeft();
                createTableBorderEndPart(columnPosition);
            }

            return this;
        }

        private boolean isNoPrintHeadActivated() {
            return argumentsHandler.getListOfArgs().stream().filter(p -> p.getOption().contains(NOPRINTHEAD)).collect(Collectors.toList()).size() != 0;
        }

        private void generateOtherArgumentsList() {

            for (int i = 0; i < argumentsHandler.getListOfArgs().size(); i++) {
                for (int j = 0; j < ArgumentsHandler.secondaryOptions.size(); j++) {
                    if (argumentsHandler.getListOfArgs().get(i).getOption().equals(ArgumentsHandler.secondaryOptions.get(j))) {
                        otherArgumentsList.add(argumentsHandler.getListOfArgs().get(i).getOption());
                    }
                }
            }
        }

        private void createHrefPart(String nameOfHref, String href) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<a href=\"")
                    .append(href)
                    .append("\">")
                    .append(nameOfHref)
                    .append("</a>")
                    .append(NEWLINE);
        }

        private void createParagraphPart(String title) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<p>")
                    .append(title)
                    .append("</p>")
                    .append(NEWLINE);
        }

        private void createParagraphPartIfEmbeddedIsHeading(String content, String modifier) {

            List<String> availableSizes = new ArrayList<>(Arrays.asList("h1","h2","h3","h4","h5","h6"));
            String type = modifier.toLowerCase(Locale.ROOT);

            if (availableSizes.contains(type)) {
                if (!otherArgumentsList.contains(NOPRINTHEADING)) {
                    sb.append(getTabsByColumnPosition(columnPosition))
                            .append("<p><")
                            .append(modifier)
                            .append(">")
                            .append(content)
                            .append("</")
                            .append(modifier)
                            .append("></p>")
                            .append(NEWLINE);
                }
            }
            else {
                throw new IllegalStateException("Invalid type error. Please use \"h1\" to \"h6\"");
            }
        }

        private void createBodyStartPart() {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<body>")
                    .append(NEWLINE);
        }

        private void createBodyEndPart() {
            sb.append(getTabsByColumnPosition(bodyPosition))
                    .append("</body>")
                    .append(NEWLINE);
        }

        private void createParagraphPartIfEmbeddedIsParagraph(String content) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<p>")
                    .append(content)
                    .append("</p>")
                    .append(NEWLINE);
        }

        private void createParagraphPartIfEmbeddIsHref(Embedded embedded) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<p><a href=\"")
                    .append(embedded.getContent())
                    .append("\">")
                    .append(embedded.getModifier())
                    .append("</a></p>")
                    .append(NEWLINE);
        }

        private void createTableTrEndPart(int columnPosition) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("</tr>")
                    .append(NEWLINE);
        }

        private void createTableBorderEndPart(int columnPosition) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("</table>")
                    .append(NEWLINE);
        }

        private void createTableTdPart(int columnPosition, List<Rows> tableRows, int i) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<td>")
                    .append(tableRows.get(i).getTitle())
                    .append("</td>")
                    .append(NEWLINE);
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<td>")
                    .append(tableRows.get(i).getParameter())
                    .append("</td>")
                    .append(NEWLINE);
        }

        private void createTableTrStartPart(int columnPosition) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<tr>")
                    .append(NEWLINE);
        }

        private void createTableBorderStartPart(int borderWidth, int columnPosition) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<table border=\"")
                    .append(borderWidth)
                    .append("px solid black\">")
                    .append(NEWLINE);
        }

        private void createDocumentHeaderPart() {
            sb.append(DOCTYPEHTML)
                    .append(NEWLINE)
                    .append(HTMLSTART)
                    .append(NEWLINE);
        }

        private void createHeadPart(String title) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append(HEADSTART)
                    .append(NEWLINE)
                    .append(getTabsByColumnPosition(columnPosition+1))
                    .append(TITLESTART)
                    .append(title)
                    .append(TITLEEND)
                    .append(NEWLINE)
                    .append(getTabsByColumnPosition(columnPosition))
                    .append(HEADLEND)
                    .append(NEWLINE);
        }

        private void createHeadingPart(String type, String title) {
            sb.append(getTabsByColumnPosition(columnPosition))
                    .append("<")
                    .append(type)
                    .append(">")
                    .append(title)
                    .append("</")
                    .append(type)
                    .append(">")
                    .append(NEWLINE);
        }

        private void moveColumnPosToLeft() {
            if (columnPosition > 0) {
                columnPosition--;
            }
        }

        private void moveColumnPosToRight() {
            columnPosition++;
        }

        public HTMLGeneratorApi close() {
            sb.append(HTMLEND);
            return new HTMLGeneratorApi(this);
        }

        private String getTabsByColumnPosition(int columnPosition) {
            return String.join("", Collections.nCopies(columnPosition, TAB));

        }
    }

}
