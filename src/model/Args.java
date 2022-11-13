package model;

public class Args {

    private final String option;
    private final String parameter;

    public Args(String option, String parameter) {
        this.option = option;
        this.parameter = parameter;
    }

    public String getOption() {
        return option;
    }

    public String getParameter() {
        return parameter;
    }

}
