import java.io.*;
import java.util.*;
// Academic Integrity Pledge is in README.md

public class Main {
    private static Player player;
    private static final boolean[][] map = new boolean[10][20]; // whether or not the player has visited
    private static final Character[][] enemies = new Character[10][20]; // all enemies on the map
    private static final Item[][] items = new Item[10][20]; // all items on the map
    private static char lastMove; // the last direction you moved in

    public static void main(String[] args) {
        Output print = new Output("log.txt"); // logs will have timestamps now
        // Note that essentially every action done in the program is with a method that returns a string.
        // this is to make logging the stuff easy, the output goes straight into the PrintWriter.
        System.out.print("Enter your name:\n> ");
        player = new Player(Input.nextLine());
        setup();

        //for (Character[] i: enemies) {for (Character j: i) {if (j != null) {System.out.println(j.getName());}}}

        // A not dead player is an alive player.
        while (!player.isDead()) {
            printMap();
            System.out.println(player.getInfo());
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
            // check if there's nothing left to do, if there is then break the loop
            boolean stuff = false;
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    stuff = (!map[i][j] || enemies[i][j] != null);
                    if (stuff) { // if not everywhere has been explored or monsters still exist
                        break; // skip the die(print) which would end the game
                    }
                }
                if (stuff) {
                    break;
                }
            }
            if (!stuff) { // if not stuff to do
                System.out.println("You see nothing else to do: congratulations, you have won");
                die(print);
            }
        }
        // When the player dies or quits the game, the loop exits and the code ends, letting all the output go to log.txt
        // in the case that it doesn't, output something useless that can be cut off
        // hopefully this works
        print.close(); // pls work (IT DOES)
    }

    /**
     * Have the player perform an action
     * @param print PrintWriter to log the action in a file
     */
    public static void act (Output print) {
        System.out.print("What do you do? (h for help)\n> ");
        //String in = input.next();
        //char action = in.charAt(0);

        // ohh fuck i just realized why i have to use java.lang.Character
        // it's becaues i overwrote the plain Character reference
        switch (Input.nextChar()) {
            case 'm' -> { // Move
                lastMove = Input.next().charAt(0); // record your step for later
                print.println(player.move(lastMove));
                map[player.getY()][player.getX()] = true; // mark tile as visited
            }
            case 'e', 'c' -> {
                player.printInventory(); // empty inventory does nothing
                System.out.printf("Which weapon do you want to equip%s?\n> ", (player.getWeapon() != null) ? ", replacing " + player.getWeapon() : "");
                if (Input.hasNextInt()) {
                    print.println(player.equip(Input.nextInt()));
                } else if (Input.hasNext()) {
                    Input.next();
                }
            }
            case 'u' -> {
                player.printInventory();
                System.out.print("Which item do you want to use?\n> ");
                if (Input.hasNextInt()) { // very similar to the above block
                    print.println(player.useItem(Input.nextInt()));
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
            case 'h' -> {
                System.out.println("Help:\n|   (M)ove (N/E/S/W U/D/L/R)\n|   (E)quip / (C)hange weapon\n|   (U)se an item\n|   (P)lay a (G)ame\n|   (Q)uit the game\n|   get (H)elp");
                print.println("Accessed the help menu");
            }
            case 'd' -> { // hidden dev commands, will remain in the game but NOT visible to the player
                switch (Input.nextChar()) {
                    case 'k' -> { // "kill all"
                        for (Character[] i : enemies) { // arrays hold reference to object
                            for (int j = 0; j < i.length; j++) {
                                i[j] = null;
                            }
                        }
                    }
                    case 'g' -> { // "give all"
                        player.acquire(new DeatherNote());
                        player.acquire(new Deather());
                        player.acquire(new Sword("Dev", 15));
                        player.acquire(new Spear("Dev", 13));
                        player.acquire(new Shovel("Dev", 12));
                        player.acquire(new Food("Dev Apple", 50));
                        player.acquire(new Card(-100));
                        player.acquire(new Card(100));
                        player.acquire(new Pill());
                    }
                    case 'p' -> { // "pickup all"
                        for (Item[] i: items) {
                            for (int j = 0; j < i.length; j++) {
                                if (i[j] != null) {
                                    player.acquire(i[j]);
                                    i[j] = null;
                                }
                            }
                        }
                    }
                    case 'v' -> { // "visit all"
                        for (boolean[] i: map) {
                            for (int j = 0; j < i.length; j++) {
                                i[j] = true;
                            }
                        }
                    }
                    case 'l' -> { // "locate all"
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[i].length; j++) {
                                if (enemies[i][j] != null) {
                                    System.out.println(String.format("%s at (%d, %d)", enemies[i][j], j + 1, i + 1));
                                }
                                if (items[i][j] != null) {
                                    System.out.println(String.format("%s at (%d, %d)", items[i][j], j + 1, i + 1));
                                }
                            }
                        }
                    }
                    case 'c' -> { // "clear inventory"
                        player.clearInv();
                    }
                }
            }
        }
        System.out.println();
    }

    /**
     * Print the map
     */
    public static void printMap () {
        //System.out.println(Arrays.toString(player.getPos()));
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (!map[i][j]) {
                    System.out.print("▄ "); // not visited, replaced the . because this is cooler
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
    public static void fight (Character enemy, Output print) {
        int turns = 0; boolean ran = false;
        print.println(player + " engaged in combat with " + enemy); // yay logs

        enemy.introduce();
        while (!(player.isDead() || enemy.isDead()) && !ran) {
            turns++; // RNG IS KEYYYY
            // More turns = more damage
            // harder fight = more reward
            // player turn
            System.out.print("What do you do? (h for help)\n> ");
            switch (Input.nextChar()) {
                case 'a' -> {
                    System.out.println();
                    print.println(player.useWeapon()); // most of the time will have no functional value, except for Deather
                    print.println(player.attack(enemy, turns));
                }
                case 'u' -> { // same block as in Main.act effectively
                    // rather dangerous though
                    player.printInventory();
                    System.out.print("Which item do you want to use?\n> ");
                    if (Input.hasNextInt()) {
                        print.println(player.useItem(Input.nextInt()));
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
                        default -> 'n'; // pointless but wtv
                    });
                    System.out.println("As you run, " + enemy + " gets a hit on you"); // this makes the dragon actually worthy of being a final boss
                    print.println(player + " ran from the fight, taking a hit");
                    print.println(enemy.attack(player, turns - 2)); // gotta be less damage because the attack is grazing you
                    System.out.println("You ran from the fight");
                    ran = true; // i have to do *this* to break the loop :sob:
                }
                case 'h' -> {
                    System.out.println("Help:\n|   (A)ttack\n|   (U)se item\n|   (R)un\n|   get (H)elp");
                    turns--; // without this you could spam 'h' to get infinite attack damage
                    continue;
                }
                default -> { // L you typed the wrong thing
                    System.out.println("You do nothing");
                    print.println(player + " did nothing");
                }
            }
            // enemy turn
            if (!enemy.isDead() && !ran) {
                print.println(enemy.attack(player, turns));
            }
        }
        if (enemy.isDead()) {
            int reward = (int)((enemy.getMaxHP() / 2) * Math.pow(turns, 1.5));
            System.out.printf("You have won the fight and received $%d in spoils\n", reward);
            print.println(String.format("%s has won the fight and received $%d in spoils", player, reward));
            player.addMoney(reward);

            // remove the enemy from the map
            enemies[player.getY()][player.getX()] = null;
        } else if (player.isDead()) {
            die(print);
        } // else nothing happens, output handled above bc this is the RUN && SURVIVE condition
    }

    /**
     * Generate the map
     */
    public static void setup () {
        // I'm a fucking idiot, I put INTEGER DIVISION for each conditional
        // so a lot of stuff just went to 0% because of TRUNCATION
        // I had to have ChatGPT tell me this :sob:

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
                    } else if (typeSeed < (5.0 / 6)) { // 1/3 for this, 1/6 for the next
                        if (specSeed < (4.0 / 9)) {
                            enemies[i][j] = new Character("Wolf", 150, 4, "A snarling Wolf bounds onto the road");
                        } else if (specSeed < (7.0 / 9)) {
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

        // make sure that the two items are not at the same position
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
                    if (typeSeed < (1.0 / 6)) { // (1 / 6) - 0 = 16.66...% chance
                        // 20% for Large, 80% for Small or nothing
                        // in order for the overall chance of Small to be 20%, 0.2*(1/0.8) = 0.25, it should be 25%
                        items[i][j] = new Item(((Math.random() < 0.2) ? "Large " : ((Math.random() < 0.25) ? "Small " : "")) + ((Math.random() < 0.5) ? "Stick" : "Rock"));
                    } else if (typeSeed < (4.0 / 6)) { // (4 / 6) - (1 / 6) = 50% chance
                        if (specSeed < 0.5) {
                            String[] names = {"Carrot", "Bread", "Baked Potato", "Golden Carrot", "Steak", "Golden Apple", "God Apple"};
                            int[] saturation = {2, 3, 6, 8, 10, 15, 25};
                            int index = (int)(Math.random() * names.length);
                            items[i][j] = new Food(names[index], saturation[index]);
                        } else if (specSeed < (2.0 / 3)) { // 2/3 - 1/2 = 1/6
                            items[i][j] = new Card((int)(Math.random() * 21) - 10);
                        } else { // 1 - (2/3) = 1/3
                            items[i][j] = new Pill();
                        }
                    } else { // 1 in 3 chance
                        String[] types = {"Wooden", "Golden", "Stone", "Iron", "Diamond", "Netherite"};
                        int[] atks = {2, 1, 4, 6, 10, 18};
                        // different rarities needs some weighting
                        // wood = 35%, gold = 10%, sotne = 25%, iron = 15%, diamond = 10%, netherite = 5%
                        double rng = Math.random(); int index;
                        if (rng < 0.35) { // + 0.35
                            index = 0;
                        } else if (rng < 0.45) { // + 0.10
                            index = 1;
                        } else if (rng < 0.7) { // + 0.25
                            index = 2;
                        } else if (rng < 0.85) { // + 0.15
                            index = 3;
                        } else if (rng < 0.95) { // + 0.10
                            index = 4;
                        } else { // remainder = 0.05
                            index = 5;
                        }

                        if (specSeed < (1.0 / 3)) {
                            items[i][j] = new Sword(types[index], atks[index] + 2);
                        } else if (specSeed < (2.0 / 3)) {
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
    public static void die (Output print) {
        System.out.println();

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
        System.out.println("You ended with $" + player.getMoney());
        System.out.println(player.getDmgStats());

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
            if (player.getMoney() < 0) {
                System.out.println("You became too greedy, as punishment you have lost all your money");
            }
            return String.format("%s rolled %s, and %dx'ed their money: %s now has $%d%s", player, Arrays.toString(original), earnings, player, player.getMoney(), (player.getMoney() < 0) ? " because of greed" : "");
        }
    }
}