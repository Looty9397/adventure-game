import java.util.*;

/**
 * Input: one place for input, primarily to avoid errors with Deather
 */
public class Input {
    // these methods are self explanatory copies of the scanner methods
    private static final Scanner input = new Scanner(System.in);

    /**
     * Scanner.next() but lowercase
     * @return lowercase of the next token
     */
    public static String next () {
        return input.next().toLowerCase();
    }

    /**
     * get the next line
     * @return da next line
     */
    public static String nextLine () {
        // not formatted because this is used for Player name, which would be wierd
        return input.nextLine();
    }

    /**
     * better version of Scanner.nextInt() since it catches the extra newline
     * @return the next int
     */
    public static int nextInt () {
        int out = input.nextInt();
        input.nextLine(); // catch the thingy
        return out;
    }

    /**
     * Scanner.next().charAt(0)
     * @return the first char of the next token
     */
    public static char nextChar () {
        return next().charAt(0);
    }

    /**
     * is there a next token?
     * @return well is ther??
     */
    public static boolean hasNext () {
        return input.hasNext();
    }

    /**
     * is the next token an int?
     * @return is it??
     */
    public static boolean hasNextInt () {
        return input.hasNextInt();
    }
}
