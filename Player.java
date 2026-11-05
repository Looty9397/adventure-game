import java.util.*;

public class Player extends Character {
    private ArrayList<Item> inventory = new ArrayList<>();
    private int x; private int y;
    private Item weapon;
    private int money;

    public Player (String name) {
        super(name, 50, 5);
    }

    public int getX () {
        return this.x;
    }

    public int getY () {
        return this.y;
    }

    public int[] getPos () {
        // rows, then cols
        return new int[]{this.y, this.x};
    }

    public int getMoney () {
        return this.money;
    }

    @Override
    public int getSTR () {
        return super.getSTR() + ((this.weapon != null) ? this.weapon.getATK() : 0);
    }

    public void addMoney (int val) {
        this.money += val;
    }

    public void loseMoney (int val) {
        if (this.money - val > 0) {
            this.money -= val;
        } else {
            this.money = 0;
        }
    }

    public void printInventory () {
        for (int i = 0; i < this.inventory.size(); i++) {
            System.out.printf("%d. %s", i, this.inventory.get(i).toString());
        }
    }

    public void equip (int i) {
        if (i < this.inventory.size() && i >= 0) {
            Item temp = this.inventory.get(i).copy();
            if (this.weapon == null) {
                this.inventory.remove(i);
            } else {
                this.inventory.set(i, this.weapon.copy());
            }
            this.weapon = temp;
        }
    }

    public void move (char dir) {
        switch (dir) {
            case 'n', 'u' -> {
                if (this.y < 9) {this.y += 1;}
            }
            case 'e', 'r' -> {
                if (this.x < 9) {this.x += 1;}
            }
            case 's', 'd' -> {
                if (this.y > 0) {this.y -= 1;}
            }
            case 'w', 'l' -> {
                if (this.x > 0) {this.x -= 1;}
            }
            default -> {
            }
        }
    }

    public void acquire (Item val) {
        this.inventory.add(val);
    }

    public void discard (int val) {
        this.inventory.remove(val);
    }
}
