import java.util.*;

/**
 * Input: one place for input, solely to avoid errors with Deather
 */
public class Input {
    private static final Scanner input = new Scanner(System.in);

    public static String next () {
        // i can format so i will
        return input.next().toLowerCase();
    }

    public static String nextLine () {
        return input.nextLine().toLowerCase();
    }

    public static int nextInt () {
        int out = input.nextInt();
        input.nextLine(); // catch the thingy
        return out;
    }

    public static char nextChar () {
        return next().toLowerCase().charAt(0);
    }

    public static boolean hasNext () {
        return input.hasNext();
    }

    public static boolean hasNextInt () {
        return input.hasNextInt();
    }
}
