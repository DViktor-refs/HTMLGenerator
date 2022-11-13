package controller;

import handlers.*;
import model.HTMLGenerator;
import view.View;

public class Controller {

    private View view;
    private HTMLGenerator model;

    public Controller(View view, HTMLGenerator model) {
        this.view = view;
        this.model = model;
    }

    public void setupArguments(String[] args) {
        ArgumentsHandler.getInstance().initialize(args);
    }

    public void displayHtml() {
        view.show(model.getHtml());
    }
}
