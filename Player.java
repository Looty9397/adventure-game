import java.util.*;

public class Player extends Character {
    private final ArrayList<Item> inventory = new ArrayList<>();
    private int x; private int y;
    private Item weapon;
    private int money;

    public Player (String name) {
        super(name, 50, 5);
        this.money = 10;
        //System.out.println(this.inventory.size());
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

    public String equip (int val) {
        if (val >= this.inventory.size() || val < 0) {
            System.out.println("Cannot equip item " + val + " which does not exist");
            return this.getName() + " tried to equip a nonexistent item";
        }else if (this.inventory.get(val).isConsumable()) {
            System.out.println("Cannot equip a consumable item");
            return this.getName() + " tried to equip a consumable item";
        } else  {
            Item temp = this.inventory.get(val).copy();
            if (this.weapon == null) {
                this.inventory.remove(val);
            } else {
                this.inventory.set(val, this.weapon.copy());
            }
            this.weapon = temp;
            return "You have equipped " + this.weapon + " with ATK " + this.weapon.getATK();
        }
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
        if (val >= this.inventory.size() || val < 0) {
            System.out.println("Cannot use item " + val + " which does not exist");
            return this.getName() + " tried to use a nonexistent item";
        }else if (this.inventory.get(val).isWeapon()) {
            System.out.println("Cannot use a weapon");
            return this.getName() + " tried to use a weapon";
        } else  {
            this.inventory.get(val).use(this);
            String out = String.format("%s used the%s item %s", this.getName(), (this.inventory.get(val).isConsumable()) ? " consumable" : "", this.inventory.get(val).toString());
            if (this.inventory.get(val).isConsumable()) {
                discard(val);
            }
            return out;
        }

    }

    public String useWeapon () {
        if (this.weapon != null) {
            this.weapon.use(this);
        }
        return this.getName() + " used their weapon " + this.weapon;
    }
}
