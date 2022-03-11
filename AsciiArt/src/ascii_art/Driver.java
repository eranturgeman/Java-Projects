package ascii_art;

import image.Image;
import java.util.logging.Logger;


/**
 * Main class, responsible for running the AsciiArt application
 */
public class Driver {
    /**
     * This is the main function. checks that a valid path to an image was received and runs the application
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: java asciiArt ");
            return;
        }
        Image img = Image.fromFile(args[0]);
        if (img == null) {
            Logger.getGlobal().severe("Failed to open image file " + args[0]);
            return;
        }
        new Shell(img).run();
    }
}
