package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class is responsible to run the AsciiArt application.
 * @author eran_turgeman
 */
public class Shell {
    // =============================== private constants ===============================
    private static final String CMD_EXIT = "exit";
    private static final String INVALID_COMMAND = "ERROR!: Invalid command";
    private static final String INVALID_COMMAND_PARAM = "ERROR!: Incorrect amount of params for this action";
    private static final int NO_PARAMS = 0;
    private static final int SINGLE_PARAM = 1;
    private static final String CHANGE_CHAR_SET_INVALID_INPUT = "ERROR!: Invalid input to %s chars command";
    private static final String INITIAL_CHAR_RANGE = "0-9";
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final String RESOLUTION_CHANGE_MESSAGE = "Width set to %d";
    private static final int RES_CHANGE_MULT_FACTOR = 2;
    private static final String RES_UP_ERROR_MESSAGE = "ERROR!: You've reached the maximal resolution";
    private static final String RES_DOWN_ERROR_MESSAGE = "ERROR!: You've reached the minimal resolution";
    private static final String CHANGE_RESOLUTION_INVALID_INPUT = "ERROR!: Invalid input to resolution " +
            "change command";
    private static final String FONT_NAME = "Courier new";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final char FIRST_ASCII_CHAR = ' ';
    private static final char LAST_ASCII_CHAR = '~';
    
    // =============================== private fields ===============================
    private final Set<Character> charSet;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private final ConsoleAsciiOutput consoleOutput;
    private int charsInRow;
    private final BrightnessImgCharMatcher charMatcher;
    private final AsciiOutput htmlOutput;
    private boolean isConsole;
    
    // =============================== private class functions ===============================
    /*
     * gets a string represents a range of ascii chars and parse it to a two cells array where the first
     * cell is the range's beginning and the second cell is the range's ending.
     * in case of invalid range- returns null
     */
    private static char[] parseCharRange(String param){
        char[] result = new char[2];
        if(param.length() == 1){
            result[0] = param.charAt(0);
            result[1] = param.charAt(0);
            return result;
        }else if(Objects.equals(param, "all")){
            result[0] = FIRST_ASCII_CHAR;
            result[1] = LAST_ASCII_CHAR;
            return result;
        }else if(Objects.equals(param, "space")) {
            result[0] = ' ';
            result[1] = ' ';
            return result;
        }else if(Objects.equals(param.charAt(1), '-') && param.length() == 3){
            result[0] = param.charAt(0);
            result[1] = param.charAt(2);
            return result;
        }else{
            return null;
        }
    }
    
    // =============================== public functions ===============================
    
    /**
     * Constructor
     * @param image the image to perform the work on
     */
    public Shell(Image image){
        this.charSet = new HashSet<>();
        changeCharSet(INITIAL_CHAR_RANGE, true);
        this.minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
        this.maxCharsInRow = image.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, this.maxCharsInRow), this.minCharsInRow);
        this.charMatcher = new BrightnessImgCharMatcher(image, FONT_NAME);
        this.htmlOutput = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        this.consoleOutput = new ConsoleAsciiOutput();
        this.isConsole = false;
    }
    
    /**
     * runs the application- asks the user for an input command, performs validations checks and performs
     * the commands if valid. upon inserting "exit" the program will be terminated.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String[] words = {""};
        String cmd;
        while (!words[0].equals(CMD_EXIT)) {
            if (!words[0].equals("")) {
                String param = "";
                if (words.length > 1) {
                    param = words[1];
                }
                
                chooseAction(words[0], param, words.length - 1);
            }
            System.out.print(">>> ");
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }
    
    // =============================== private functions ===============================
    /*
     * a selection function for the given input command. performs validation on the number of arguments
     * given and performs the requested command. prints an error message in case of errors.
     */
    private void chooseAction(String word, String param, int paramsAmount) {
        boolean error = false;
        switch (word){
            case "chars":
                if(paramsAmount != NO_PARAMS){
                    error = true;
                    break;
                }
                showChars();
                break;
            case "add":
                if(paramsAmount != SINGLE_PARAM){
                    error = true;
                    break;
                }
                changeCharSet(param, true);
                break;
            case "remove":
                if(paramsAmount != SINGLE_PARAM){
                    error = true;
                    break;
                }
                changeCharSet(param, false);
                break;
            case "res":
                if(paramsAmount != SINGLE_PARAM){
                    error = true;
                    break;
                }
                resChange(param);
                break;
            case "console":
                if(paramsAmount != NO_PARAMS){
                    error = true;
                    break;
                }
                isConsole = true;
                break;
            case "render":
                if(paramsAmount != NO_PARAMS){
                    error = true;
                    break;
                }
                render();
                break;
            default:
                System.out.println(INVALID_COMMAND);
        }
        if(error){
            System.out.println(INVALID_COMMAND_PARAM);
        }
    }
    
    /*
     * prints to the screes the current set of Ascii chars that can be rendered
     */
    private void showChars() {
        charSet.stream().sorted().forEach(character -> System.out.print(character + " "));
        System.out.println();
    }
    
    /*
     * adds or removes a single char or a range of chars to the chars set. validates the given provided
     * range is valid.
     */
    private void changeCharSet(String param, boolean isAddition){
        char[] range = parseCharRange(param);
        if(range != null && (int)range[0] > (int)range[1]){
            char temp = range[0];
            range[0] = range[1];
            range[1] = temp;
        }
        if(range != null && isAddition){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::add);
        }else if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::remove);
        }else{
            String action;
            if(isAddition){
                action = "add";
            }else{
                action = "remove";
            }
            System.out.println(String.format(CHANGE_CHAR_SET_INVALID_INPUT, action));
        }
    }
    
    /*
     * changes the resolution of the ascii image. validates that we are within the range of min/max
     * resolution possible.
     * prints the current resolution after every update.
     * prints an error message in case of an error.
     */
    private void resChange(String param){
        switch(param){
            case "up":
                if(charsInRow * RES_CHANGE_MULT_FACTOR > maxCharsInRow){
                    System.out.println(RES_UP_ERROR_MESSAGE);
                }else{
                    charsInRow *= RES_CHANGE_MULT_FACTOR;
                    System.out.println(String.format(RESOLUTION_CHANGE_MESSAGE, charsInRow));
                }
                break;
            case "down":
                if(charsInRow / RES_CHANGE_MULT_FACTOR < minCharsInRow){
                    System.out.println(RES_DOWN_ERROR_MESSAGE);
                }else{
                    charsInRow /= RES_CHANGE_MULT_FACTOR;
                    System.out.println(String.format(RESOLUTION_CHANGE_MESSAGE, charsInRow));
                }
                break;
            default:
                System.out.println(CHANGE_RESOLUTION_INVALID_INPUT);
        }
    }
    
    /*
     * rendering the current image with the current set of chars and current resolution to the console of
     * html file
     */
    private void render(){
        Character[] setAsArray = new Character[charSet.size()];
        char[][] outputImage = charMatcher.chooseChars(charsInRow, charSet.toArray(setAsArray));
        if(outputImage == null || outputImage.length == 0){
            return;
        }
        if(isConsole){
            consoleOutput.output(outputImage);
        }else{
            htmlOutput.output(outputImage);
        }
    }
}
