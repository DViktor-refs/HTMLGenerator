import controller.Controller;
import model.HTMLGenerator;
import view.ConsoleView;

public class Launcher {

    public static void main(String[] args) {

        Controller controller = new Controller(new ConsoleView(), new HTMLGenerator());
        controller.setupArguments(args);
        controller.displayHtml();

    }
}
