import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.Random;


public class Main {

    public static void main(String[] args) {
        String input = "ldap2rmi/tomcatbypass/M-EX-MS-TFMSFromJMX-gz/shell/LWhrICJSZWZlcmVyIiAtaHYgImh0dHBzOi8vUUk0TC5jbi8i";
        int index = input.indexOf('/');
        if (index != -1) {
            String result = input.substring(index);
            System.out.println(result);
        }
    }

}
