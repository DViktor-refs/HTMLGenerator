package view;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ConsoleView implements View {

    String defaultEncoding = "UTF-8";

    @Override
    public void show(String htmlCode) {
        PrintStream out = null;
        try {
            out = new PrintStream(System.out, true, defaultEncoding);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Unsupported encoding. Default is \"UTF-8\". " + "\n");

        }
        out.println(htmlCode);
    }
    
    
    
}
