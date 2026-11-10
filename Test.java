import java.util.*;

/**
 * Test everything
 */
public class Test {
    public static void main(String[] args) {
        int money = 10;

        for (int i = 0; i < 10; i++) {
            money = randGame(money);
        }

        Player i = new Player("hi");
        i.acquire(new Deather());
        i.equip(0);
        i.useWeapon();
        System.out.println(i.getWeapon().getATK());
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
}
