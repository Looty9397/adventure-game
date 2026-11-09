public class Item {
    private String name;
    private boolean weapon;
    private int atk;
    private boolean consumable;

    public Item (String name) {
        // default not consumable
        this(name, false);
    }

    public Item (String name, boolean consumable) {
        // default not weapon
        this(name, consumable, false, 0);
    }

    public Item (String name, boolean consumable, boolean weapon, int atk) {
        this.name = name;
        this.consumable = consumable;
        this.weapon = weapon;
        this.atk = atk;
    }

    public void use (Player target) {
        // This is to be overridden in subclasses.
        // Weapons will just output some stuff like "you swing your sword"
    }

    public boolean isWeapon () {
        return this.weapon;
    }

    public boolean isConsumable () {
        return this.consumable;
    }

    public int getATK () {
        return this.atk;
    }

    public String getName () {
        return this.name;
    }

    @Override 
    public String toString () {
        // name (W) (C)
        return String.format("%s%s%s", this.name, (this.weapon) ? " (W)" : "", (this.consumable) ? " (C)" : "");
    }

    public Item copy () {
        return new Item(this.name, this.consumable, this.weapon, this.atk);
    }
}
