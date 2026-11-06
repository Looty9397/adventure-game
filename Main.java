import java.io.*;
import java.util.*;

public class Main {
    private static Player player;
    private static final boolean[][] map = new boolean[10][10];
    private static final Character[][] enemies = new Character[10][10];
    private static final Item[][] items = new Item[10][10];
    private static char lastMove;

    public static void main(String[] args) {
        File f = new File("log.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        } else {
            try (PrintWriter print = new PrintWriter(f)) {
                print.print(""); // Clear the file
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        // ChatGPT was used here to correct NoSuchElementException
        // new FileWriter(f, true) makes it be in append mode so its not just continuously overwriting.
        try (PrintWriter print = new PrintWriter(new FileWriter(f, true)); Scanner input = new Scanner(System.in)) {
            System.out.print("Enter your name:\n> ");
            player = new Player(input.nextLine());
            setup();

            while (!player.isDead()) {
                printMap();
                act(print, input);
                if (items[player.getY()][player.getX()] != null) {
                    player.acquire(items[player.getY()][player.getX()]);
                    print.println(player.getName() + " picked up " + items[player.getY()][player.getX()]);
                    items[player.getY()][player.getX()] = null;
                }
                if (enemies[player.getY()][player.getX()] != null) {
                    fight(enemies[player.getY()][player.getX()], print, input);
                }
            }
            // When the player dies or quits the game, the loop exits and the code ends, letting all the output go to log.txt
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void act (PrintWriter print, Scanner input) {
        System.out.print("What do you want to do? (M)ove <dir>, (E)quip / (C)hange weapon, (U)se an item, (P)lay a (G)ame, or (Q)uit the game?\n> ");
        String in = input.next();
        char action = in.charAt(0);

        switch (java.lang.Character.toLowerCase(action)) {
            case 'm' -> {
                if (!input.hasNext()) {System.out.print("Which direction? (N/E/S/W U/D/L/R)\n> ");}
                lastMove = input.next().charAt(0);
                print.println(player.move(lastMove));
                map[player.getY()][player.getX()] = true;
            }
            case 'e', 'c' -> {
                player.printInventory();
                System.out.printf("Which weapon do you want to equip%s?\n> ", (player.getWeapon() != null) ? ", replacing" + player.getWeapon() : "");
                if (input.hasNextInt()) {
                    print.println(player.equip(input.nextInt()));
                    input.nextLine();
                } else if (input.hasNext()) {
                    input.next();
                }
            }
            case 'u' -> {
                player.printInventory();
                System.out.print("Which item do you want to use?\n> ");
                if (input.hasNextInt()) {
                    print.println(player.useItem(input.nextInt()));
                    input.nextLine();
                } else if (input.hasNext()) {
                    input.next();
                }
            }
            case 'p', 'g' -> {
                print.println(randGame());
            }
            case 'q' -> {
                die(print);
            }
        }
    }

    public static void printMap () {
        //System.out.println(Arrays.toString(player.getPos()));
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (!map[i][j]) {
                    System.out.print(". ");
                } else if (enemies[i][j] != null) {
                    System.out.print("X ");
                } else if (player.getX() == j && player.getY() == i) {
                    System.out.print("P ");
                } else {
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }

    public static void fight (Character enemy, PrintWriter print, Scanner input) {
        int turns = 0;
        print.println(player + " engaged in combat with " + enemy);
        while (!(player.isDead() || enemy.isDead())) {
            turns++; // RNG IS KEYYYY
            int pDmg = (int)(player.getSTR() * Math.pow(turns, 1.5) * ((Math.random() * 0.5) + 0.5));
            int eDmg = (int)(enemy.getSTR() * Math.pow(turns, 1.5) * ((Math.random() * 0.5) + 0.5));
            // player turn
            System.out.print("What do you want to do? (A)ttack, (U)se item, (R)un\n> ");
            char action = input.next().charAt(0);
            switch (java.lang.Character.toLowerCase(action)) {
                case 'a' -> {
                    print.println(player.useWeapon()); // most of the time will do nothing, except for Deather (thanks Keniel for inspiration)
                    System.out.printf("You attack for %d damage\n", pDmg);
                    System.out.printf("%s now has %d/%d hp\n", enemy.toString(), enemy.getHP() - pDmg, enemy.getMaxHP());
                    print.printf("%s attacks for %d damage\n", player.toString(), pDmg);
                    print.printf("%s now has %d/%d hp\n", enemy.toString(), enemy.getHP() - pDmg, enemy.getMaxHP());
                    enemy.setHP(enemy.getHP() - pDmg);
                }
                case 'u' -> {
                    player.printInventory();
                    System.out.print("Which item do you want to use?\n> ");
                    if (input.hasNextInt()) {
                        print.println(player.useItem(input.nextInt()));
                        input.nextLine();
                    } else if (input.hasNext()) {
                        input.next();
                    }
                }
                case 'r' -> {
                    player.move(switch (lastMove) {
                        case 'n', 'u' -> 's';
                        case 'e', 'r' -> 'w';
                        case 's', 'd' -> 'n';
                        case 'w', 'l' -> 'e';
                        default -> 'n';
                    });
                    System.out.println("You ran from the fight");
                    print.println(player + " ran from the fight");
                    return;
                }
                default -> {
                    System.out.println("You do nothing");
                    print.println(player + " did nothing");
                }
            }
            // enemy turn
            if (!enemy.isDead()) {
                System.out.printf("%s attacks you for %d damage\n", enemy.toString(), eDmg);
                System.out.printf("You now have %d/%d hp\n", player.getHP() - eDmg, player.getMaxHP());
                print.printf("%s attacks %s for %d damage\n", enemy.toString(), player.toString(), eDmg);
                print.printf("%s now has %d/%d hp\n", player.toString(), player.getHP() - eDmg, player.getMaxHP());
                player.setHP(player.getHP() - eDmg);
            }
        }
        if (enemy.isDead()) {
            int reward = (int)((enemy.getMaxHP() / 2) * Math.pow(turns, 1.5));
            System.out.printf("%s has died\nYou have won the fight\n", enemy.toString());
            System.out.println("You received $" + reward + " in spoils");
            print.printf("%s has died\n%s has won the fight\n", enemy.toString(), player.toString());
            print.printf("%s received $%d in spoils\n", player.toString(), reward);
            player.addMoney(reward);

            // remove the enemy from the map
            enemies[player.getY()][player.getX()] = null;
        } else { // other result is player.isDead() == true
            die(print);
        }
    }

    public static void setup () {
        // Spawn monsters
        // just a blank for now, testing
        enemies[1][1] = new Character("blank", 50, 5);

        // Summon items
        // some more blanks for testing
        items[1][1] = new Item("apple");
        items[1][2] = new Item("gapple", true);
        items[2][1] = new Item("sword", false, true, 5);

        int x = -1; int y = -1; // to be used for spawning the player
        for (int i = 0; i < map.length; i++) { // start the x and y at an item or monster to avoid errors
            for (int j = 0; j < map[i].length; j++) {
                if (enemies[i][j] != null || items[i][j] != null) {
                    x = j; y = i; break;
                }
            }
            if (x != -1) {
                break;
            }
        }
        // Place the player
        while (enemies[y][x] != null || items[y][x] != null) {
            x = (int)(Math.random() * map.length);
            y = (int)(Math.random() * map.length);
        }
        player.setPos(new int[]{y, x});
        map[player.getY()][player.getX()] = true;
    }

    public static void die (PrintWriter print) {
        // stats
        int actions = 0;
        File f = new File("log.txt");
        try (Scanner log = new Scanner(f)) {
            while (log.hasNextLine()) {
                log.nextLine();
                actions++;
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        System.out.println("You completed a total of " + actions + " actions");
        System.out.println("You had $" + player.getMoney() + " when you " + ((player.isDead()) ? "died" : "quit"));

        if (player.isDead()) {
            System.out.println("You have died: Thank you for playing");
            print.println(player + " has died: Thank you for playing");
        } else {
            System.out.println("Thank you for playing");
            print.println("Thank you for playing");
        }

        // kill the player so that the loop in main knows to break, closing the PrintWriter
        player.setHP(player.getHP() - player.getMaxHP() - 1);
    }

    public static String randGame () {
        if (player.getMoney() <= 0) {
            System.out.println("You cannot play a game without money");
            return player.getName() + " tried to gamble but had no money";
        } else {
            int[] rolls = new int[]{
                (int)(Math.random() * 6),
                (int)(Math.random() * 6),
                (int)(Math.random() * 6),
                (int)(Math.random() * 6),
                (int)(Math.random() * 6),
                (int)(Math.random() * 6)
            };
            int[] original = Arrays.copyOf(rolls, rolls.length);
            Arrays.sort(rolls);

            int earnings = 0;
            if (rolls[0] == rolls[5] || rolls[0] == rolls[4] || rolls[1] == rolls[5]) {
                earnings = (int)(Math.random() * 5) + 6; // 6 - 10
            } else if ((rolls[0] == rolls[2] && rolls[3] == rolls[5])
                        || rolls[0] == rolls[2]
                        || rolls[1] == rolls[3]
                        || rolls[2] == rolls[4]
                        || rolls[3] == rolls[5]) {
                earnings = (int)(Math.random() * 4) + 4; // 4 - 7
            } else if ((rolls[0] == rolls[1] && rolls[2] == rolls[3] && rolls[4] == rolls[5])) {
                earnings = 4;
            } else if ((rolls[0] == rolls[1] && (rolls[2] == rolls[3] || rolls[3] == rolls[4] || rolls[4] == rolls[5]))
                        || (rolls[1] == rolls[2] && (rolls[3] == rolls[4] || rolls[4] == rolls[5]))
                        || (rolls[2] == rolls[3] && rolls[4] == rolls[5])) {
                earnings = (int)(Math.random() * 2) + 2; // 2 - 3
            } else if (rolls[0] == rolls[1] || rolls[1] == rolls[2] || rolls[2] == rolls[3] || rolls[3] == rolls [4] || rolls[4] == rolls[5]) {
                earnings = 1;
            }
            if (earnings > 0) {
                player.addMoney(player.getMoney() * (earnings - 1));
            } else {
                player.loseMoney(player.getMoney());
            }
            System.out.printf("You rolled %s, and you %dx'ed your money\n", Arrays.toString(original), earnings);
            return String.format("%s rolled %s, and %dx'ed their money\n", player.getName(), Arrays.toString(original), earnings);
        }
    }

}