import java.util.*;

public class Main {
    private static Player player;
    private static boolean[][] map = new boolean[10][10];
    private static Character[][] enemy = new Character[10][10];
    private static Item[][] item = new Item[10][10];

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Enter your name:\n> ");
            player = new Player(input.nextLine());
        }

        System.out.println(player.getSTR());
    }
}