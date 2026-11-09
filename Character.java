/**
 * Character: base for all characters
 */
public class Character {
    private int hp;
    private int maxHP;
    private int str;
    private String name;
    private String intro;

    /**
     * Create a character without any intro
     * @param name
     * @param maxHP
     * @param str
     */
    public Character (String name, int maxHP, int str) {
        this(name, maxHP, str, "");
    }

    /**
     * Create a complete character
     * @param name
     * @param maxHP
     * @param str
     * @param intro
     */
    public Character (String name, int maxHP, int str, String intro) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = this.maxHP;
        this.intro = intro;
        this.str = str;
    }

    /**
     * talk
     */
    public void introduce () {
        System.out.println(this.intro);
    }

    public int getHP () {
        return this.hp;
    }

    public int getMaxHP () {
        return this.maxHP;
    }

    public int getSTR () {
        return this.str;
    }

    public String getName () {
        return this.name;
    }

    /**
     * sets hp, not more than maxHP though
     * @param val
     */
    public void setHP (int val) {
        if (val > this.maxHP) {
            this.hp = this.maxHP;
        } else {
            this.hp = val;
        }
    }

    /**
     * heal some damage
     * @param val
     */
    public void heal (int val) {
        if (this.hp + val > this.maxHP) {
            this.hp = maxHP;
        } else {
            this.hp += val;
        }
    }

    public boolean isDead () {
        return this.hp <= 0;
    }

    @Override 
    public String toString () {
        return String.format("%s: %d/%dhp", this.name, this.hp, this.maxHP);
    }
}
