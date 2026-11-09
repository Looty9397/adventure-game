import java.util.*;

public class Player extends Character {
    private final ArrayList<Item> inventory = new ArrayList<>();
    private int x; private int y;
    private Item weapon;
    private int money;

    /**
     * Create a player
     * @param name Only thing player needs to know
     */
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

    /**
     * get the position as an array
     * @return the position, as int[] {y, x}
     */
    public int[] getPos () {
        // rows, then cols
        return new int[]{this.y, this.x};
    }

    /**
     * setter for the position array
     * @param val array to set position
     */
    public void setPos (int[] val) {
        this.y = val[0];
        this.x = val[1];
    }

    public int getMoney () {
        return this.money;
    }

    public Item getWeapon () {
        // is the superclass because weapons aren't dedicated sub-subclasses of Item
        // not like Item -> Weapon -> all weapon object
        // it's Item -> all weapon objects
        return this.weapon;
    }

    @Override
    public int getSTR () {
        // for players you have a weapon, so you need to include it in calculations
        // orig STR stat can be got by player.getSTR() - ((player.getWeapon() != null) ? player.getWeapon().getATK() : 0)
        return super.getSTR() + ((this.weapon != null) ? this.weapon.getATK() : 0);
    }

    public void addMoney (int val) {
        this.money += val;
    }

    /**
     * lose some money, can't go below 0
     * @param val money to lose
     */
    public void loseMoney (int val) {
        if (this.money - val > 0) {
            this.money -= val;
        } else {
            this.money = 0;
        }
    }

    /**
     * print a formatted list for the inventory
     */
    public void printInventory () {
        for (int i = 0; i < this.inventory.size(); i++) {
            // #. item
            System.out.printf("%d. %s\n", i, this.inventory.get(i).toString());
        }
    }

    /**
     * swap the player's weapon
     * @param val index of weapon to swap
     * @return log string
     */
    public String equip (int val) {
        if (val >= this.inventory.size() || val < 0) { // sorry you can't equip item# -1
            System.out.println("Cannot equip item " + val + " which does not exist");
            return this.getName() + " tried to equip a nonexistent item";
        } else if (this.inventory.get(val).isConsumable()) { // nor can you fight with spaghetti
            System.out.println("Cannot equip a consumable item");
            return this.getName() + " tried to equip a consumable item";
        } else {
            Item temp = this.inventory.get(val).copy(); // to prevent weird stuff with memory
            if (this.weapon == null) {
                this.inventory.remove(val);
            } else {
                this.inventory.set(val, this.weapon.copy());
            }
            this.weapon = temp;
            System.out.println("You have equipped " + this.weapon);
            return this.getName() + " has equipped " + this.weapon + " with ATK " + this.weapon.getATK();
        }
    }

    /**
     * move the player
     * @param dir direction to move in
     * @return log string
     */
    public String move (char dir) {
        switch (java.lang.Character.toLowerCase(dir)) {
            case 'n', 'u' -> { // north, up
                if (this.y > 0) {
                    this.y -= 1;
                }
            }
            case 'e', 'r' -> { // east, right
                if (this.x < 9) {
                    this.x += 1;
                }
            }
            case 's', 'd' -> { // south, down
                if (this.y < 9) {
                    this.y += 1;
                }
            }
            case 'w', 'l' -> { // west, left
                if (this.x > 0) {
                    this.x -= 1;
                }
            }
            default -> { // L you can't give accurate input
                return this.getName() + " tried to move in direction " + java.lang.Character.toUpperCase(dir);
            }
        }

        this.heal(1); // passive regen
        return this.getName() + " moved in direction " + java.lang.Character.toUpperCase(dir);
    }

    /**
     * put something in inventory
     * @param val that object
     */
    public void acquire (Item val) {
        this.inventory.add(val);
    }

    /**
     * drop an item forever
     * @param val index of item to drop
     */
    public void discard (int val) {
        this.inventory.remove(val);
    }

    /**
     * use an item in your inventory
     * @param val index of item to use
     * @return log string
     */
    public String useItem (int val) {
        if (val >= this.inventory.size() || val < 0) { // sorry again, can't use item# -2147483648
            System.out.println("Cannot use item " + val + " which does not exist");
            return this.getName() + " tried to use a nonexistent item";
        } else if (this.inventory.get(val).isWeapon()) { // can't use a weapon because those are combat-specific abilities
            System.out.println("Cannot use a weapon");
            return this.getName() + " tried to use a weapon";
        } else {
            this.inventory.get(val).use(this); // you are the player, so you use the item on yourself.
            // as such, there are never going to be debuff items or instant-damage items
            // because only you can be targetted by them
            // and why would you kill yourself
            // just a bad idea
            String out = String.format("%s used the%s item %s\n", this.getName(), (this.inventory.get(val).isConsumable()) ? " consumable" : "", this.inventory.get(val).toString());
            // prepping a output string beforehand because it might not exist anymore when returning the log str
            if (this.inventory.get(val).isConsumable()) {
                discard(val); // oopsie daisy you consumed the consumable
            }
            return out;
        }
    }

    /**
     * useItem but for weapons, in combat
     * @return log string
     */
    public String useWeapon () {
        if (this.weapon != null) {
            this.weapon.use(this); // might be some flavor text in the output, or something else entirely (DEATHER)
        }
        return this.getName() + " used their weapon " + this.weapon;
    }
}
