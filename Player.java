import java.util.*;

public class Player extends Character {
    private final ArrayList<Item> inventory = new ArrayList<>();
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

    public void setPos (int[] val) {
        this.y = val[0];
        this.x = val[1];
    }

    public int getMoney () {
        return this.money;
    }

    public Item getWeapon () {
        return this.weapon;
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
            System.out.printf("%d. %s\n", i, this.inventory.get(i).toString());
        }
    }

    public String equip (int i) {
        if (this.inventory.get(i).isConsumable()) {
            return "Cannot equip a consumable item.";
        }
        if (i < this.inventory.size() && i >= 0) {
            Item temp = this.inventory.get(i).copy();
            if (this.weapon == null) {
                this.inventory.remove(i);
            } else {
                this.inventory.set(i, this.weapon.copy());
            }
            this.weapon = temp;
        }
        return "You have equipped " + this.weapon + " with ATK " + this.weapon.getATK();
    }

    public String move (char dir) {
        switch (java.lang.Character.toLowerCase(dir)) {
            case 'n', 'u' -> {
                if (this.y > 0) {
                    this.y -= 1;
                }
            }
            case 'e', 'r' -> {
                if (this.x < 9) {
                    this.x += 1;
                }
            }
            case 's', 'd' -> {
                if (this.y < 9) {
                    this.y += 1;
                }
            }
            case 'w', 'l' -> {
                if (this.x > 0) {
                    this.x -= 1;
                }
            }
            default -> {
                return this.getName() + " tried to move in direction " + java.lang.Character.toUpperCase(dir);
            }
        }

        return this.getName() + " moved in direction " + java.lang.Character.toUpperCase(dir);
    }

    public void acquire (Item val) {
        this.inventory.add(val);
    }

    public void discard (int val) {
        this.inventory.remove(val);
    }

    public String useItem (int val) {
        if (this.inventory.get(val).isWeapon()) {
            return this.getName() + " tried to use a weapon";
        }
        this.inventory.get(val).use(this);
        String out = this.getName() + " used the item " + this.inventory.get(val);
        if (this.inventory.get(val).isConsumable()) {
            discard(val);
        }
        return out;
    }

    public String useWeapon () {
        if (this.weapon != null) {
            this.weapon.use(this);
        }
        return this.getName() + " used their weapon " + this.weapon;
    }
}
