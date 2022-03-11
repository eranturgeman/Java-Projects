package ascii_art;

import java.util.*;


public class Algorithms {
    private static final char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p',
            'q','r','s','t','u','v','w','x','y','z'};
    private static final String[] morseLetters = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....",
            "..", ".---", "-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-", "...-",".--", "-..-",
            "-.--","--.."};

    public static int findDuplicate(int[] numList){
        int slowRunner = numList[0];
        int fastRunner = numList[numList[0]];

    
        while (fastRunner != slowRunner) {
            slowRunner = numList[slowRunner];
            fastRunner = numList[numList[fastRunner]];
        }
    
        slowRunner = 0;
        while (fastRunner != slowRunner) {
            slowRunner = numList[slowRunner];
            fastRunner = numList[fastRunner];
        }
        return slowRunner;
    }
    
    public static int uniqueMorseRepresentations(String[] words){
        HashMap<Character, String> matcher = new HashMap<>();
        for(int i = 0; i < letters.length; i++){
            matcher.put(letters[i], morseLetters[i]);
        }
        HashSet<String> unique = new HashSet<>();
        for(String word:words){
            StringBuilder morseRep = new StringBuilder();
            for(int i = 0; i < word.length(); i++){
                morseRep.append(matcher.get(word.charAt(i)));
            }
            unique.add(morseRep.toString());
        }
        return unique.size();
    }
}
