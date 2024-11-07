import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Random;


public class Main {

    public static void main(String[] args) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            char letter = (char) (random.nextInt(26) + 'a');
            sb.append(letter);
        }

        String randomLetters = sb.toString();
        System.out.println("随机字母: " + randomLetters);
    }

}
