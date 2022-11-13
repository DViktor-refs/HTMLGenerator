package model;

public class Embedded {

    private int type;
    private String content;
    private String modifier;

    public Embedded(int type, String content, String modifier) {
        this.type = type;
        this.content = content;
        this.modifier = modifier;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getModifier() {
        return modifier;
    }
}
