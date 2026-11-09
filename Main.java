import java.io.*;
import java.util.*;

public class Main {
    private static Player player;
    private static final boolean[][] map = new boolean[10][10]; // whether or not the player has visited
    private static final Character[][] enemies = new Character[10][10]; // all enemies on the map
    private static final Item[][] items = new Item[10][10]; // all items on the map
    private static char lastMove; // the last direction you moved in

    public static void main(String[] args) {
        File f = new File("log.txt");
        if (!f.exists()) { // Make sure the file is created
            try { // Or, at least make sure its blank
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
            // Note that essentially every action done in the program is with a method that returns a string.
            // this is to make logging the stuff easy, the output goes straight into the PrintWriter.
            System.out.print("Enter your name:\n> ");
            player = new Player(input.nextLine());
            setup();

            // A not dead player is an alive player.
            while (!player.isDead()) {
                printMap();
                act(print, input);
                if (items[player.getY()][player.getX()] != null) { // pick up item
                    player.acquire(items[player.getY()][player.getX()]);
                    System.out.println("You picked up " + items[player.getY()][player.getX()]);
                    print.println(player.getName() + " picked up " + items[player.getY()][player.getX()]);
                    items[player.getY()][player.getX()] = null;
                }
                if (enemies[player.getY()][player.getX()] != null) { // fight, after item
                    fight(enemies[player.getY()][player.getX()], print, input);
                }
            }
            // When the player dies or quits the game, the loop exits and the code ends, letting all the output go to log.txt
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Have the player perform an action
     * @param print PrintWriter to log the action in a file
     * @param input Scanner to get user input
     */
    public static void act (PrintWriter print, Scanner input) {
        System.out.print("What do you want to do? (M)ove <dir>, (E)quip / (C)hange weapon, (U)se an item, (P)lay a (G)ame, or (Q)uit the game?\n> ");
        //String in = input.next();
        //char action = in.charAt(0);

        switch (java.lang.Character.toLowerCase(input.next().charAt(0))) {
            case 'm' -> { // Move
                if (!input.hasNext()) {System.out.print("Which direction? (N/E/S/W U/D/L/R)\n> ");}
                lastMove = input.next().charAt(0); // record your step for later
                print.println(player.move(lastMove));
                map[player.getY()][player.getX()] = true; // mark tile as visited
            }
            case 'e', 'c' -> {
                player.printInventory(); // empty inventory does nothing
                System.out.printf("Which weapon do you want to equip%s?\n> ", (player.getWeapon() != null) ? ", replacing" + player.getWeapon() : "");
                if (input.hasNextInt()) {
                    print.println(player.equip(input.nextInt()));
                    input.nextLine(); // catch the extra line from nextInt()
                } else if (input.hasNext()) {
                    input.next();
                }
            }
            case 'u' -> {
                player.printInventory();
                System.out.print("Which item do you want to use?\n> ");
                if (input.hasNextInt()) { // very similar to the above block
                    print.println(player.useItem(input.nextInt()));
                    input.nextLine();
                } else if (input.hasNext()) {
                    input.next();
                }
            }
            case 'p', 'g' -> { // Lets go GAMBLINGGGGG
                print.println(randGame());
            }
            case 'q' -> {
                die(print); // exit the program + kill the player so the main loop (while (!player.isDead())) ends
            }
        }
    }

    /**
     * Print the map
     */
    public static void printMap () {
        //System.out.println(Arrays.toString(player.getPos()));
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (!map[i][j]) {
                    System.out.print(". "); // not visited
                } else if (enemies[i][j] != null) {
                    System.out.print("X "); // enemy on that tile (if you ran)
                    // no need for an "item on that tile" condition because you just pick up all the items you see
                } else if (player.getX() == j && player.getY() == i) {
                    System.out.print("P "); // player on that tile
                } else {
                    System.out.print("_ "); // visited tile
                }
            }
            System.out.println(); // new line :D
        }
    }

    /**
     * Mortal Kombat between the player and any other enemy
     * @param enemy the being that fights the player
     * @param print PrintWriter to log the actions in a file
     * @param input Scanner to get user input
     */
    public static void fight (Character enemy, PrintWriter print, Scanner input) {
        int turns = 0;
        print.println(player + " engaged in combat with " + enemy); // yay logs

        enemy.introduce();
        while (!(player.isDead() || enemy.isDead())) {
            turns++; // RNG IS KEYYYY
            // More turns = more damage
            // harder fight = more reward
            int pDmg = (int)(player.getSTR() * Math.pow(turns, 1.5) * ((Math.random() * 0.5) + 0.5));
            int eDmg = (int)(enemy.getSTR() * Math.pow(turns, 1.5) * ((Math.random() * 0.5) + 0.5));
            // player turn
            System.out.print("What do you want to do? (A)ttack, (U)se item, (R)un\n> ");
            switch (java.lang.Character.toLowerCase(input.next().charAt(0))) {
                case 'a' -> {
                    print.println(player.useWeapon()); // most of the time will have no functional value, except for Deather (thanks Keniel for inspiration)
                    System.out.printf("You attack for %d damage\n", pDmg);
                    System.out.printf("%s now has %d/%d hp\n", enemy.toString(), enemy.getHP() - pDmg, enemy.getMaxHP());
                    print.printf("%s attacks for %d damage\n", player.toString(), pDmg);
                    print.printf("%s now has %d/%d hp\n", enemy.toString(), enemy.getHP() - pDmg, enemy.getMaxHP());
                    enemy.setHP(enemy.getHP() - pDmg);
                }
                case 'u' -> { // same block as in Main.act effectively
                    player.printInventory();
                    System.out.print("Which item do you want to use?\n> ");
                    if (input.hasNextInt()) {
                        print.println(player.useItem(input.nextInt()));
                        input.nextLine();
                    } else if (input.hasNext()) {
                        input.next();
                    }
                }
                case 'r' -> { // move in the reverse of lastMove
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
                default -> { // L you typed the wrong thing
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

    /**
     * Generate the map
     */
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

    /**
     * peacefully kill off the player
     * @param print PrintWriter to log in the file
     */
    public static void die (PrintWriter print) {
        // read the stats
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

        // display stats
        System.out.println("You completed a total of " + actions + " actions");
        System.out.println("You had $" + player.getMoney() + " when you " + ((player.isDead()) ? "died" : "quit"));

        // thanks for playing
        if (player.isDead()) {
            System.out.println("You have died: Thank you for playing");
            print.println(player + " has died: Thank you for playing");
        } else {
            System.out.println("Thank you for playing");
            print.println("Thank you for playing");
        }

        // kill the player so that the loop in main knows to break, closing the PrintWriter
        player.setHP(player.getHP() - player.getMaxHP() - 1);
        // the - 1 is there just to guarantee that the player is below 0 hp
    }

    /**
     * GAMBLING
     * @return thingy to log, could have gone in another class but that's a stretch goal
     */
    public static String randGame () {
        if (player.getMoney() <= 0) {
            System.out.println("You cannot play a game without money");
            return player.getName() + " tried to gamble but had no money";
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
                player.addMoney(player.getMoney() * (earnings - 1));
            } else {
                player.loseMoney(player.getMoney());
            }
            System.out.printf("You rolled %s, and you %dx'ed your money\nYou now have $%d\n", Arrays.toString(original), earnings, player.getMoney());
            return String.format("%s rolled %s, and %dx'ed their money\n%s now has $%d\n", player.getName(), Arrays.toString(original), earnings, player.getName(), player.getMoney());
        }
    }
}