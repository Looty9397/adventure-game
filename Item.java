/**
 * Item: base for all items
 */
public class Item {
    private String name;
    private boolean weapon;
    private int atk;
    private boolean consumable;

    /**
     * create an item, that is neither consumable nor a weapon
     * @param name name of item
     */
    public Item (String name) {
        // default not consumable
        this(name, false);
    }

    /**
     * create an item, that could be consumable but is not a weapon
     * @param name name of item
     * @param consumable if consumable
     */
    public Item (String name, boolean consumable) {
        // default not weapon
        this(name, false, 0);
        this.consumable = consumable; // override the consumable assignment from the Item(String, boolean, int) constructor
    }

    /**
     * create an item, that could be a weapon, but is not a consumable
     * @param name name of item
     * @param weapon if is weapon
     * @param atk atk of item
     */
    public Item (String name, boolean weapon, int atk) {
        this.name = name;
        this.consumable = false;
        this.weapon = weapon;
        this.atk = atk;
    }

    /**
     * use the item on the player
     * @param target dat playa
     */
    public void use (Player target) {
        // This is to be overridden in subclasses.
        // Weapons will just output some stuff like "you swing your sword"
        System.out.println("You toss the " + name + " in your hand");
    }

    /**
     * is it a weapon?
     * @return if is weapon
     */
    public boolean isWeapon () {
        return this.weapon;
    }

    /**
     * is it edible?
     * @return if is consumable
     */
    public boolean isConsumable () {
        return this.consumable;
    }

    /**
     * how much atk does it have
     * @return its atk
     */
    public int getATK () {
        return this.atk;
    }

    /**
     * how to call it what
     * @return name
     */
    public String getName () {
        return this.name;
    }

    @Override 
    public String toString () {
        // name (W) (C)
        return String.format("%s%s%s", this.name, (this.weapon) ? " (W)" : "", (this.consumable) ? " (C)" : "");
    }

    /**
     * return a copy of the item, for encapsulation
     * @return copy of the item
     */
    public Item copy () {
        if (this.weapon) {
            return new Item(this.name, this.weapon, this.atk);
        } else if (this.consumable) {
            return new Item(this.name, this.consumable);
        } else {
            return new Item(this.name);
        }
    }
}
