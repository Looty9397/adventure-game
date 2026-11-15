import java.io.*;
import java.util.*;

/**
 * Test everything
 */
public class Test {
    public static void main(String[] args) {
        int total = 0;
        for (String i: args) {
            File f = new File(i);
            int lineCount = 0;
            try (Scanner input = new Scanner(f)) {
                while (input.hasNextLine()) {
                    lineCount++;
                    input.nextLine();
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
            System.out.println(i + ": " + lineCount + " lines");
            total += lineCount;
        }
        System.out.println(total + " total lines");

        System.out.println("HelloWorld".indexOf("Z"));

        System.out.println(remove("The_code_in_this_repo_was_all_written_by_looty_9397", "_"));
    }

    public static int randGame (int money) {
        if (money <= 0) {
            System.out.println("You cannot play a game without money");
        } else {
            int[] rolls = new int[]{ // 6x d6
                (int)(Math.random() * 6) + 1,
                (int)(Math.random() * 6) + 1,
                (int)(Math.random() * 6) + 1,
                (int)(Math.random() * 6) + 1,
                (int)(Math.random() * 6) + 1,
                (int)(Math.random() * 6) + 1
            };
            int[] original = Arrays.copyOf(rolls, rolls.length);
            Arrays.sort(rolls); // for ease of calculation

            int earnings = 0;
            if (rolls[0] == rolls[5] || rolls[0] == rolls[4] || rolls[1] == rolls[5]) {
                // six of a kind or five or a kind
                earnings = (int)(Math.random() * 5) + 6; // 6 - 10
            } else if (rolls[0] == rolls[3] || rolls[1] == rolls[4] || rolls[2] == rolls[5]  || rolls[0] == rolls[2] || rolls[1] == rolls[3] || rolls[2] == rolls[4] || rolls[3] == rolls[5]) {
                // four or three of a kind
                earnings = (int)(Math.random() * 4) + 4; // 4 - 7
            } else if ((rolls[0] == rolls[1] && rolls[2] == rolls[3] && rolls[4] == rolls[5])) {
                // three pair
                earnings = 4;
            } else if ((rolls[0] == rolls[1] && (rolls[2] == rolls[3] || rolls[3] == rolls[4] || rolls[4] == rolls[5]))
                        || (rolls[1] == rolls[2] && (rolls[3] == rolls[4] || rolls[4] == rolls[5]))
                        || (rolls[2] == rolls[3] && rolls[4] == rolls[5])) {
                // two pair
                earnings = (int)(Math.random() * 2) + 2; // 2 - 3
            } else if (rolls[0] == rolls[1] || rolls[1] == rolls[2] || rolls[2] == rolls[3] || rolls[3] == rolls [4] || rolls[4] == rolls[5]) {
                // pair
                earnings = 1;
            }
            if (earnings > 0) {
                money *= earnings;
            } else {
                money = 0;
            }
            System.out.printf("You rolled %s, and you %dx'ed your money\n", Arrays.toString(original), earnings);
        }
        return money;
    }

    public static String remove (String message, String str) {
        while (message.indexOf(str) >= 0) {
            String outMessage = message.substring(0, message.indexOf(str));
            if (message.indexOf(str) + str.length() < message.length()) {
                outMessage += message.substring(message.indexOf(str) + str.length());
            }
            message = outMessage;
        }
        
        return message;
    }
}
