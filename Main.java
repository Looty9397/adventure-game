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
        try (PrintWriter print = new PrintWriter(new FileWriter(f, true))) {
            // Note that essentially every action done in the program is with a method that returns a string.
            // this is to make logging the stuff easy, the output goes straight into the PrintWriter.
            System.out.print("Enter your name:\n> ");
            player = new Player(Input.nextLine());
            setup();

            // A not dead player is an alive player.
            while (!player.isDead()) {
                printMap();
                act(print);
                if (items[player.getY()][player.getX()] != null) { // pick up item
                    player.acquire(items[player.getY()][player.getX()]);
                    System.out.println("You picked up " + items[player.getY()][player.getX()]);
                    print.println(player.getName() + " picked up " + items[player.getY()][player.getX()]);
                    items[player.getY()][player.getX()] = null;
                }
                if (enemies[player.getY()][player.getX()] != null) { // fight, after item
                    fight(enemies[player.getY()][player.getX()], print);
                }
            }
            // When the player dies or quits the game, the loop exits and the code ends, letting all the output go to log.txt
            // in the case that it doesn't, output something useless that can be cut off
            print.println("ENDOFFILE");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Have the player perform an action
     * @param print PrintWriter to log the action in a file
     */
    public static void act (PrintWriter print) {
        System.out.print("What do you want to do? (M)ove <dir>, (E)quip / (C)hange weapon, (U)se an item, (P)lay a (G)ame, or (Q)uit the game?\n> ");
        //String in = input.next();
        //char action = in.charAt(0);

        // ohh fuck i just realized why i have to use java.lang.Character
        // it's becaues i overwrote the plain Character reference
        switch (java.lang.Character.toLowerCase(Input.next().charAt(0))) {
            case 'm' -> { // Move
                if (!Input.hasNext()) {System.out.print("Which direction? (N/E/S/W U/D/L/R)\n> ");}
                lastMove = Input.next().charAt(0); // record your step for later
                print.println(player.move(lastMove));
                map[player.getY()][player.getX()] = true; // mark tile as visited
            }
            case 'e', 'c' -> {
                player.printInventory(); // empty inventory does nothing
                System.out.printf("Which weapon do you want to equip%s?\n> ", (player.getWeapon() != null) ? ", replacing" + player.getWeapon() : "");
                if (Input.hasNextInt()) {
                    print.println(player.equip(Input.nextInt()));
                    Input.nextLine(); // catch the extra line from nextInt()
                } else if (Input.hasNext()) {
                    Input.next();
                }
            }
            case 'u' -> {
                player.printInventory();
                System.out.print("Which item do you want to use?\n> ");
                if (Input.hasNextInt()) { // very similar to the above block
                    print.println(player.useItem(Input.nextInt()));
                    Input.nextLine();
                } else if (Input.hasNext()) {
                    Input.next();
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
     */
    public static void fight (Character enemy, PrintWriter print) {
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
            switch (java.lang.Character.toLowerCase(Input.next().charAt(0))) {
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
                    if (Input.hasNextInt()) {
                        print.println(player.useItem(Input.nextInt()));
                        Input.nextLine();
                    } else if (Input.hasNext()) {
                        Input.next();
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
            if (!enemy.isDead() && !player.isDead()) { // checks for player because Pill can insta-kill on player's turn
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
        // ALL MONSTERS:
        //     Tank: Orc, Wolf, Golem
        //     Glass Cannon: Goblin, Bandit
        //     Balanced: Adventurer, Dragon (boss)
        // NOTE: They don't really do anything so there's no need for subclasses. the default class will suffice
        int[] dragonPos = {(int)(Math.random() * map.length), (int)(Math.random() * map.length)}; // only one will exist
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < enemies[i].length; j++) {
                if (Arrays.compare(new int[]{i, j}, dragonPos) == 0) {
                    enemies[i][j] = new Character("Dragon", 1000, 20, "A glorious Dragon blocks your path");
                } else if (Math.random() < 0.2) { // slightly rarer than items
                    double typeSeed = Math.random();
                    double specSeed = Math.random();
                    if (typeSeed < 0.5) {
                        if (specSeed < 0.6) {
                            enemies[i][j] = new Character("Goblin", 25, 10, "A Goblin emerges from the brush"); // don't let him live
                        } else {
                            enemies[i][j] = new Character("Bandit", 75, 6, "A Bandit appears behind you"); // hes human so tankier
                        }
                    } else if (typeSeed < (5 / 6)) { // 1/3 for this, 1/6 for the next
                        if (specSeed < (4 / 9)) {
                            enemies[i][j] = new Character("Wolf", 150, 4, "A snarling Wolf bounds onto the road");
                        } else if (specSeed < (7 / 9)) {
                            enemies[i][j] = new Character("Orc", 250, 3, "A lone Orc lumbers towards you");
                        } else {
                            enemies[i][j] = new Character("Golem", 500, 2, "A massive Golem impedes your advance");
                        }
                    } else {
                        enemies[i][j] = new Character("Adventurer", 100, 6, "A fellow Adventurer challenges you");
                    }
                }
            }
            
        }

        // Summon items
        // ALL ITEMS:
        //     Consumable: Card, Food, Pill (CHECK)
        //     Weapon: Deather, Shovel, Spear, Sword (CHECK)
        //     Neither: DeatherNote, Item (CHECK)
        int[][] deatherPos = { // There will only be one of each of these on the map, so they must have set places to spawn
            {(int)(Math.random() * map.length), (int)(Math.random() * map.length)}, // DeatherNote
            {(int)(Math.random() * map.length), (int)(Math.random() * map.length)} //  Deather
        };

        while (Arrays.compare(deatherPos[0], deatherPos[1]) == 0) {
            // no need to shuffle the other, it's position is good as is
            deatherPos[1] = new int[]{(int)(Math.random() * map.length), (int)(Math.random() * map.length)};
        }

        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < items[i].length; j++) {
                if (Arrays.compare(new int[]{i, j}, deatherPos[0]) == 0) {
                    items[i][j] = new DeatherNote();
                } else if (Arrays.compare(new int[]{i, j}, deatherPos[1]) == 0) {
                    items[i][j] = new Deather();
                } else if (Math.random() < 0.25) { // one in four chance given not a deather or deathernote
                    double typeSeed = Math.random();
                    double specSeed = Math.random();
                    if (typeSeed < (1 / 3)) { // (1 / 3) - 0 = 33.33...% chance
                        // 20% for Large, 80% for Small or nothing
                        // in order for the overall chance of Small to be 20%, 0.2*(1/0.8) = 0.25, it should be 25%
                        items[i][j] = new Item(((Math.random() < 0.2) ? "Large " : ((Math.random() < 0.25) ? "Small " : "")) + ((Math.random() < 0.5) ? "Stick" : "Rock"));
                    } else if (typeSeed < (5 / 6)) { // (5 / 6) - (1 / 3) = 50% chance
                        if (specSeed < (1 / 2)) {
                            String[] names = {"Bread", "Apple", "Steak", "Carrot", "Golden Apple"};
                            int[] saturation = {5, 2, 8, 1, 15};
                            int index = (int)(Math.random() * names.length);
                            items[i][j] = new Food(names[index], saturation[index]);
                        } else if (specSeed < (2 / 3)) { // 2/3 - 1/2 = 1/6
                            items[i][j] = new Card((int)(Math.random() * 21) - 10);
                        } else { // 1 - (2/3) = 1/3
                            items[i][j] = new Pill();
                        }
                    } else { // 1 in 6 chance
                        String[] types = {"Wooden", "Golden", "Stone", "Iron", "Diamond", "Netherite"};
                        int[] atks = {2, 1, 4, 5, 7, 10};
                        // different rarities needs some weighting
                        // wood = 35%, gold = 10%, sotne = 25%, iron = 15%, diamond = 10%, netherite = 5%
                        double rng = Math.random(); int index;
                        if (rng < 0.35) {index = 0;}
                        else if (rng < 0.45) {
                            index = 1;
                        } else if (rng < 0.7) {
                            index = 2;
                        } else if (rng < 0.85) {
                            index = 3;
                        } else if (rng < 0.95) {
                            index = 4;
                        } else {
                            index = 5;
                        }

                        if (specSeed < (1 / 3)) {
                            items[i][j] = new Sword(types[index], atks[index] + 2);
                        } else if (specSeed < (2 / 3)) {
                            items[i][j] = new Spear(types[index], atks[index]);
                        } else {
                            items[i][j] = new Shovel(types[index], atks[index] - 1);
                        }
                    }
                }
            }
        }

        // Summon the player
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
     * GAMBLING, could be in a separate class (only Main method that isnt a void) but that's a stretch goal
     * @return thingy to log
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