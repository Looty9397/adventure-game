import java.util.*;

/**
 * Player
 */
public class Player extends Character {
    private final ArrayList<Item> inventory = new ArrayList<>();
    private int x; private int y;
    private Item weapon;
    private int money;
    private final int[] dmg = new int[2];

    /**
     * Create a player
     * @param name Only thing player needs to know
     */
    public Player (String name) {
        super(name, 50, 5);
        this.money = 10;
        //System.out.println(this.inventory.size());
    }

    /**
     * whats the players x
     * @return x
     */
    public int getX () {
        return this.x;
    }

    /**
     * whats the players y
     * @return y
     */
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

    /**
     * how much money does player have
     * @return $$$$
     */
    public int getMoney () {
        return this.money;
    }

    /**
     * what is the player's weapon
     * @return weapon
     */
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

    /**
     * get richer
     * @param val mor money
     */
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
            System.out.printf("%d. %s\n", i, this.inventory.get(i));
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
        } else if (!this.inventory.get(val).isWeapon()) { // nor can you fight with paper
            System.out.println("Cannot equip something that is not a weapon");
            return this.getName() + " tried to equip something that is not a weapon";
        } else {
            Item temp = this.inventory.get(val).copy(); // to prevent weird stuff with memory
            // ChatGPT helped at this point since .copy() is not overridden, this would nullify the subclass overrides.
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
        boolean success = false;
        switch (java.lang.Character.toLowerCase(dir)) {
            case 'n', 'u' -> { // north, up
                if (this.y > 0) {
                    this.y -= 1;
                    success = true;
                }
            }
            case 'e', 'r' -> { // east, right
                if (this.x < 19) {
                    this.x += 1;
                    success = true;
                }
            }
            case 's', 'd' -> { // south, down
                if (this.y < 9) {
                    this.y += 1;
                    success = true;
                }
            }
            case 'w', 'l' -> { // west, left
                if (this.x > 0) {
                    this.x -= 1;
                    success = true;
                }
            }
            default -> {} // L you can't give accurate input, leaves success as false
        }

        if (success) {
            this.heal(1); // passive regen
            // ~~NOTE: this passive regen CANNOT increase the max hp because the formula is (maxhp + (hp + heal)) / 2 and so the 1/2 gets truncated~~
            // THIS DOES INCREASE MAX HP
            return this.getName() + " moved in direction " + java.lang.Character.toUpperCase(dir);
        } else {
            System.out.println("You cannot move in that direction");
            return this.getName() + " tried to move in direction " + java.lang.Character.toUpperCase(dir) + " but failed";
        }
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
            String out = String.format("%s used the%s item %s", this.getName(), (this.inventory.get(val).isConsumable()) ? " consumable" : "", this.inventory.get(val));
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

    /**
     * heal some damage, overheal increases your max hp
     * @param val
     */
    public void heal (int val) {
        if (this.getHP() + val > this.getMaxHP()) {
            this.setMaxHP((this.getMaxHP() + (this.getHP() + val) + (int)(Math.random() * 2)) / 2);
            // ~~+1 is there so that walking can buf your maxHP~~
            // + (int)(Math.random() * 2) is there for a 50% chance of ^, if an odd number, otherwise will change nothing
            // (1 + 0) / 2 -> 0; (1 + 1) / 2 -> 1; (2 + 1) / 2 -> 3 / 2 -> 1
            this.setHP(this.getMaxHP());
        } else {
            this.setHP(this.getHP() + val);
        }
    }

    /**
     * quick little string method
     * @return player hp and money
     */
    public String getInfo () {
        return String.format("%s %d/%d hp $%d", this, this.getHP(), this.getMaxHP(), this.money);
    }

    /**
     * clear inventory
     */
    public void clearInv () {
        this.inventory.clear();
    }

    /**
     * take some damage
     */
    public void takeDmg (int eDmg) {
        this.setHP(this.getHP() - eDmg);
        this.dmg[1] += eDmg;
    }

    /**
     * attack a monster
     * @param target the monster
     * @param turns multiplier
     * @return output string
     */
    public String attack (Character target, int turns) { // moved from Main.java
        int pDmg = (int)(this.getSTR() * Math.pow(1.5, turns - 1) * ((Math.random() * 0.2) + 0.9));
        // ^ calculated here instead of before so that the changes from useWeapon() will apply
        // Player damage is less variable so you can be more confident
        if (Math.random() < 0.1) { // crits, 10% for 125-150% damage
            pDmg = (int)(pDmg * ((Math.random() * 0.25) + 1.25));
        } // holy buffing the player
        target.setHP(target.getHP() - pDmg);
        this.dmg[0] += pDmg;
        System.out.printf("You attack for %d damage: %s now has %d/%d hp\n", pDmg, target, target.getHP(), target.getMaxHP());
        return String.format("%s attacks for %d damage: %s now has %d/%d hp", this, pDmg, target, target.getHP(), target.getMaxHP());
    }

    /**
     * give a damage stats string to print to the console
     * @return hi
     */
    public String getDmgStats () {
        return String.format("You dealt a total of %d damage, and took a total of %d damage", dmg[0], dmg[1]);
    }
}
