package ascii_art.img_to_char;


import image.Image;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * responsible to perform a conversion of an Image object to Ascii picture using brightness matching between
 * a given pixel (pixels) to an ascii character from a predefined set with the closest brightness value
 * @see Image
 * @author eran_turgeman
 */
public class BrightnessImgCharMatcher {
    //========================================= private constants =========================================
    private static final double RED_CONVERSION_FACTOR = 0.2126;
    private static final double BLUE_CONVERSION_FACTOR = 0.0722;
    private static final double GREEN_CONVERSION_FACTOR = 0.7152;
    private static final int RGB_HIGHEST_VALUE = 255;
    private static final int CHAR_IMAGE_SIZE = 16;
    private static final int DUMMY_VALUE_FOR_EQUAL_MIN_MAX = 1;
    
    //========================================= private fields =========================================
    private final Image image;
    private final String font;
    private final HashMap<Image, Double> cache = new HashMap<>();
    
    //========================================= public functions =========================================
    
    /**
     * Constructor
     * @param image a king of an Image object (any class implementing Image interface)
     * @param font the requested font to the ascii chars conversion
     */
    public BrightnessImgCharMatcher(Image image, String font){
        this.image = image;
        this.font = font;
    }
    
    /**
     * returns an array of chars that represents a picture
     * @param numCharsInRow amount of chars to draw in a line
     * @param charSet the set of chars we want to draw the picture with
     * @return 2-Dim array representing a picture
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        if(charSet == null || numCharsInRow <= 0){
            return null;
        }
        if(charSet.length == 0){
            return new char[0][0];
        }
        
        double[] charsBrightness = getCharsBrightness(charSet, CHAR_IMAGE_SIZE);
        double[] charsNormalizedBrightness = normalizeBrightness(charsBrightness);
        CharDoublePair[] charsWithBrightness = new CharDoublePair[charSet.length];
        
        for(int i = 0; i < charsWithBrightness.length; i++){
            charsWithBrightness[i] = new CharDoublePair(charSet[i], charsNormalizedBrightness[i]);
        }
        mergeSort(charsWithBrightness, charsBrightness.length);
        return convertImageToAscii(charsWithBrightness, numCharsInRow);
    }
    
    //========================================= private functions =========================================
    /*
     * this function gets a sorted array or pairs (char & it's normalized brightness ) with the requested
     * amount to characters in a row and creates the actual ascii image from the Image object got in the
     * constructor
     */
    private char[][] convertImageToAscii(CharDoublePair[] charsWithBrightness, int numCharsInRow){
        int pixels = image.getWidth() / numCharsInRow;
        int asciiArrRows = image.getHeight() / pixels;
        int asciiArrCols = image.getWidth() / pixels;
        
        char[][] asciiArt = new char[asciiArrRows][asciiArrCols];
        int i = 0, j = 0;
        
        for(Image subImage: image.squareSubImagesOfSize(pixels)){
            double imageAverageBrightness;
            if(cache.containsKey(subImage)){
                imageAverageBrightness = cache.get(subImage);
            }else{
                imageAverageBrightness = getImageAverageBrightness(subImage);
                cache.put(subImage, imageAverageBrightness);
            }
            
            char representative = findClosestBrightnessChar(imageAverageBrightness, charsWithBrightness);
            asciiArt[i][j++] = representative;
            if(j == asciiArrCols){
                j = 0;
                i++;
            }
        }
        return asciiArt;
    }
    
    /*
     * given a brightness value looks for a char in the pairs array which it's brightness value is the
     * closest to the given value and return this char.
     * This method is based on binary search with adjustment to find the closest value to the given brightness
     */
    private char findClosestBrightnessChar(double imageAverageBrightness,
                                           CharDoublePair[] charsWithBrightness) {
        int arrayLength = charsWithBrightness.length;
        if(imageAverageBrightness <= charsWithBrightness[0].getValue()){
            return charsWithBrightness[0].getKey();
        }
        if(charsWithBrightness[arrayLength - 1].getValue() <= imageAverageBrightness){
            return charsWithBrightness[arrayLength - 1].getKey();
        }
        
        int i = 0, mid = 0;
        int j = arrayLength;
        
        while(i < j){
            mid = (i + j) / 2;
            if(charsWithBrightness[mid].getValue() == imageAverageBrightness){
                return charsWithBrightness[mid].getKey();
            }
            
            if(charsWithBrightness[mid].getValue() > imageAverageBrightness){
                if(mid > 0 && charsWithBrightness[mid - 1].getValue() < imageAverageBrightness){
                    return findClosestToTarget(mid, mid - 1, imageAverageBrightness, charsWithBrightness);
                }
                j = mid;
            }
            else {
                if(mid < arrayLength - 1 && charsWithBrightness[mid + 1].getValue() > imageAverageBrightness){
                    return findClosestToTarget(mid, mid + 1, imageAverageBrightness, charsWithBrightness);
                }
                i = mid + 1;
            }
        }
        return charsWithBrightness[mid].getKey();
    }
    
    /*
     * helper function to findClosestBrightnessChar
     */
    private char findClosestToTarget(int firstInd, int secondInd, double targetBrightness,
                                     CharDoublePair[] charsWithBrightness) {
        if(Math.abs(charsWithBrightness[firstInd].getValue() - targetBrightness) <
                Math.abs(charsWithBrightness[secondInd].getValue() - targetBrightness)){
            return charsWithBrightness[firstInd].getKey();
        }
        return charsWithBrightness[secondInd].getKey();
    }
    
    /*
     * given an Image object- calculate it's average brightness after converting the pixels to grey pixels
     */
    private double getImageAverageBrightness(Image image){
        long pixelsAmount = 0;
        double pixelsGreyNormalizedSum = 0;
        for(Color pixel: image.pixels()){
            pixelsAmount++;
            pixelsGreyNormalizedSum += (pixel.getRed() * RED_CONVERSION_FACTOR +
                    pixel.getBlue() * BLUE_CONVERSION_FACTOR +
                    pixel.getGreen() * GREEN_CONVERSION_FACTOR) / RGB_HIGHEST_VALUE;
        }
        return pixelsGreyNormalizedSum / pixelsAmount;
    }
    
    /*
     * given an array of doubles representing chars brightnesses- normalizing those values to be in range
     * [0,1] according to a given normalization formula
     */
    private double[] normalizeBrightness(double[] charsBrightness) {
        double maxValue = charsBrightness[0];
        double minValue = charsBrightness[0];
        
        for(double val:charsBrightness){
            if(val > maxValue){
                maxValue = val;
            }
            if(val < minValue){
                minValue = val;
            }
        }
        
        double[] result = new double[charsBrightness.length];
        if(maxValue == minValue){
            Arrays.fill(result, DUMMY_VALUE_FOR_EQUAL_MIN_MAX);
            return result;
        }
        for(int i = 0; i < charsBrightness.length; i++){
            result[i] = (charsBrightness[i] - minValue) / (maxValue - minValue);
        }
        return result;
    }
    
    /*
     * given an array of chars, requested font and requested amount of chars per row- calculates and
     * returns an array of each char's brightness according to a given formula
     */
    private double[] getCharsBrightness(Character[] charSet, int numOfCharsInRow){
        double[] result = new double[charSet.length];
        double totalPixels = numOfCharsInRow * numOfCharsInRow;
    
        for(int i = 0; i < charSet.length; i++){
            int whitePixels = countWhitePixels(charSet[i], numOfCharsInRow);
            result[i] = (double)whitePixels / totalPixels;
        }
        return result;
    }
    
    /*
     * helper function that counts the amount of white pixels in an ascii representation of a character,
     * according to a given font
     */
    private int countWhitePixels(Character character, int numOfCharInRow) {
        boolean[][] charImg = CharRenderer.getImg(character, numOfCharInRow, font);
        int counter = 0;
        
        for(boolean[] row: charImg){
            for(boolean cell: row){
                if(cell){
                    counter++;
                }
            }
        }
        return counter;
    }
    
    /*
     * merge sort algorithm
     */
    private void mergeSort(CharDoublePair[] charsWithBrightness, int arrayLength){
        if(arrayLength < 2){
            return;
        }
        int mid = arrayLength / 2;
        CharDoublePair[] left = new CharDoublePair[mid];
        CharDoublePair[] right = new CharDoublePair[arrayLength - mid];

        System.arraycopy(charsWithBrightness, 0, left, 0, mid);

        for(int i = mid; i < arrayLength; i++){
            right[i - mid] = charsWithBrightness[i];
        }
        mergeSort(left, mid);
        mergeSort(right, arrayLength - mid);
        merge(charsWithBrightness, left, right, mid, arrayLength - mid);
    }
    
    /*
     * merge algorithm - helper for mergeSort function
     */
    private void merge(CharDoublePair[] fullArray, CharDoublePair[] leftArray, CharDoublePair[] rightArray,
                       int leftLength, int rightLength){
        int i = 0, j = 0, k = 0;
        while(i < leftLength && j < rightLength){
            if(leftArray[i].getValue() <= rightArray[j].getValue()){
                fullArray[k++] = leftArray[i++];
            }else{
                fullArray[k++] = rightArray[j++];
            }
        }
        while(i < leftLength) {
            fullArray[k++] = leftArray[i++];
        }
        while(j < rightLength){
            fullArray[k++] = rightArray[j++];
        }
    }
    
    /*
     * helper class used to enable binding a character with its brightness so the work with the characters
     * and their brightnesses will be easier and more elegant.
     * Notice: I know a better implementation will be a generic class (and even better- using sorted
     * hashmap, but it is not allowed) but for the sake of this part I chose to implement it like that (I
     * saw that in the second part of the ex we can use Collections so this class might not be needed)
     */
    private static class CharDoublePair implements Comparable<CharDoublePair>{
        private final Character key;
        private final double value;
    
        /*
         * Constructor
         * @param c a character
         * @param val a double value
         */
        public CharDoublePair(Character c, double val){
            this.key = c;
            this.value = val;
        }
    
        /*
         * Getter for the key
         * @return the pair's key
         */
        public Character getKey() {
            return key;
        }
    
        /*
         * Getter for the value
         * @return the pair's value
         */
        public double getValue() {
            return value;
        }
    
        /*
         * an override to the compare function for this class to enable later usage in existing sort functions
         * @param other the pair to compare to
         * @return an int value represents which pair is smaller than the other
         */
        @Override
        public int compareTo(CharDoublePair other) {
            double comparisonResult = this.value - other.value;
            if(comparisonResult < 0){
                return -1;
            }else if(comparisonResult > 0){
                return 1;
            }
            return 0;
        }
    }
}
