package handlers;

import customexceptions.*;
import model.Args;
import model.Rows;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static consts.FieldNames.*;

public class ArgumentsHandler {

    List<Args> listOfArgs = new ArrayList<>();
    private final List<String> argsList = new ArrayList<>();
    private static final ArgumentsHandler instance = new ArgumentsHandler();

    private static final String[] strAvailableOptions =
            new String[]{"-name", "-help", "-email","-noprintparagraph","-noprinttd",
            "-noprinthref", "-noprinttable","-noprinthead","-noprinttr","-noprintheading"};

    private static final String[] strSecondaryOptions =
            new String[]{"-noprintparagraph","-noprinttd", "-noprinthref",
             "-noprinttable","-noprinthead","-noprinttr","-noprintheading"};

    public static final List<String> availableOptions = new ArrayList<>(Arrays.asList(strAvailableOptions));
    public static final List<String> secondaryOptions = new ArrayList<>(Arrays.asList(strSecondaryOptions));


    private ArgumentsHandler(){}

    public static ArgumentsHandler getInstance() {
        return instance;
    }

    public void initialize(String[] args) {
        argsList.addAll(Arrays.asList(args));
        checkArgs(argsList);
    }

    public List<Rows> createTableElementsFromArgs(ArgumentsHandler argumentsHandler) {
        List<Rows> result = new ArrayList<>();

        if (argumentsHandler.getListOfArgs().size() == 1) {
            System.out.println(argumentsHandler.getHelpText());
            System.exit(0);
        }
        else {
            if (argumentsHandler.getListOfArgs().size() >= 2) {
                for (int i = 0; i < argumentsHandler.getListOfArgs().size(); i++) {
                    if (argumentsHandler.getListOfArgs().get(i).getOption().contains(NAME)) {
                        result.add(new Rows("Név", argumentsHandler.getListOfArgs().get(i).getParameter()));
                    }
                    if (argumentsHandler.getListOfArgs().get(i).getOption().contains(EMAIL)) {
                        result.add(new Rows("Elérhetőség", argumentsHandler.getListOfArgs().get(i).getParameter()));
                    }
                }
            }
            else {
                System.out.println("\nPlease initalize ArgumentsHandler first.\n");
                throw new InvalidArgumentListException("ArgumentsHandler doesn't initalized.");
            }
        }
        return result;
    }

    public List<Args> getListOfArgs() {
        if (!listOfArgs.isEmpty()) {
            return listOfArgs;
        }
        else {
            System.out.println("\nYou have to add name and email arguments. Please use -help argument and check the help documentation.\n");
            throw new InvalidArgumentListException("Needed arguments missing.");
        }
    }

    public String getHelpText() {
        //<editor-fold desc="Help text">
        return "\nHTMLGeneratorApi help text" + "\n\n"
                + "Usage :  java -jar HTMLGeneratorApi.jar [OPTION] [\"PARAMETER\"] [OTHER OPTIONS]... " + "\n\n"
                + "you must enter your username and your email address Or use -help." + "\n"
                + "Other options are not mandatory for operation." + "\n\n"
                + "For example: java -jar HTMLGeneratorApi.jar -name \"John Doe\" -email \"john@abcdemail.com\"" + "\n\n"
                + "Options: " + "\n"
                + "   -name \"YOUR NAME\" " + "\n"
                + "   -email \"YOUR EMAIL\"" + "\n"
                + "   -help" + "\n\n"
                + "Secondary options: " + "\n"
                + "   -noprintheader" + "   " + "Use it if you want to neglect headers." + "\n"
                + "   -noprintparagraph" + "   " + "Use it if you want to neglect paragraphs." + "\n"
                + "   -noprinthref" + "   " + "Use it if you want to neglect hrefs." + "\n"
                + "   -noprinttable" + "   " + "Use it if you want to neglect tables." + "\n"
                + "   -noprinttr" + "   " + "Use it if you want to neglect table rows." + "\n"
                + "   -noprinttd" + "   " + "Use it if you want to neglect table data elements." + "\n\n"
                + "you have to add your username and your email address between quation marks. " + "\n";
        //</editor-fold>
    }

    private void checkArgs(List<String> argsList) {
        if (isArgslistContainsMinimalParameters(argsList)) {
            listOfArgs = findArguments(argsList);
        }
        else {
            System.out.println("\nYou have to add name and email arguments. Please use -help argument and check the help documentation.\n");
            throw new InvalidArgumentListException("Needed arguments missing.");
        }
    }

    private List<Args> findArguments(List<String> argsList) {
        List<Args> result = new ArrayList<>();

        if (!argsList.contains(HELP)) {

            for (int i = 0; i < argsList.size(); i++) {

                int indexOfNameFieldOption = argsList.indexOf(NAME);
                int indexOfNameFieldParameter = argsList.indexOf(NAME)+1;
                int indexOfEmailFieldOption = argsList.indexOf(EMAIL);
                int indexOfEmailFieldParameter = argsList.indexOf(EMAIL)+1;

                if (argsList.get(i).contains(NAME)
                        && argsList.size() > indexOfNameFieldParameter
                        && isValidNameField(argsList.get(indexOfNameFieldParameter))) {
                    result.add(new Args(argsList.get(indexOfNameFieldOption) , argsList.get(indexOfNameFieldParameter)));
                }

                if (argsList.get(i).contains(EMAIL)
                        && argsList.size() > indexOfEmailFieldParameter
                        && isValidEmailAddress(argsList.get(indexOfEmailFieldParameter))) {
                    result.add(new Args(argsList.get(indexOfEmailFieldOption) , argsList.get(indexOfEmailFieldParameter)));
                }

                for (int j = 0; j < secondaryOptions.size(); j++) {
                    if (argsList.get(i).equals(secondaryOptions.get(j))) {
                        result.add(new Args(argsList.get(i), ""));
                    }
                }
            }
        }
        else {
            result.add(new Args(HELP, ""));
        }
        return result;
    }

    private boolean isArgslistContainsMinimalParameters(List<String> argsList) {
        return  !argsList.isEmpty()
                && (argsList.contains(NAME)
                && argsList.contains(EMAIL))
                || argsList.contains(HELP);
    }

    private boolean isValidNameField(String tempName) {

        if(tempName.trim().length()>0
                && !tempName.contains(EMAIL)
                && !tempName.contains(HELP)
                && checkQuationMarks(tempName)) {
            return true;
        }
        else {
            System.out.println("\nYou added an invalid -name parameter. Please use -help argument and check the help documentation.\n");
            throw new InvalidNameException("Invalid name format.");
        }
    }

    private boolean isValidEmailAddress(String emailAddress) {

        if (isEmailAddressFormatCorrect(emailAddress)) {
            return true;
        }
        else {
            System.out.println("\nYou added an invalid -email parameter. Please use -help argument and check the help documentation.\n");
            throw new InvalidEmailAddressException("Invalid email address format.");
        }
    }

    private boolean isEmailAddressFormatCorrect(String emailAddress) {
        boolean result;
        result = !checkIfParameterIsInTheseWords(emailAddress, availableOptions);
        result = checkQuationMarks(emailAddress);
        result = checkEmailMatcher(emailAddress);
        return result;
    }

    private boolean checkEmailMatcher(String emailAddress) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    private boolean checkIfParameterIsInTheseWords(String s, List<String> availablePointers) {
        return availablePointers.contains(s);
    }

    private boolean checkQuationMarks(String s) {
        return !s.substring(0, 1).contains("\"") || !s.substring(s.length() - 1).contains("\"");
    }

}
