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
     * @param name name
     * @param maxHP maxHP
     * @param str strength
     */
    public Character (String name, int maxHP, int str) {
        this(name, maxHP, str, "");
    }

    /**
     * Create a complete character
     * @param name name
     * @param maxHP maxHP
     * @param str strength
     * @param intro introduction phrase
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

    /**
     * getHP
     * @return current hp
     */
    public int getHP () {
        return this.hp;
    }

    /**
     * getMaxHP
     * @return max hp
     */
    public int getMaxHP () {
        return this.maxHP;
    }

    /**
     * setMaxHP
     * @param val new value
     */
    public void setMaxHP (int val) {
        this.maxHP = val;
    }

    /**
     * getSTR
     * @return strength of character
     */
    public int getSTR () {
        return this.str;
    }

    /**
     * getName
     * @return name of character
     */
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
     * if character is dead
     * @return if character is dead
     */
    public boolean isDead () {
        return this.hp <= 0;
    }

    @Override 
    public String toString () {
        return this.name;
    }

    /**
     * attack the player
     * @param target player to attack
     * @param turns multiplier value
     * @return string to go to log
     */
    public String attack (Player target, int turns) {
        int eDmg = (int)(this.str * Math.pow(1.5, turns - 1) * ((Math.random() * 0.5) + 0.5));
        if (Math.random() < 0.1) { // crits, 10% for 100-125% damage
            eDmg = (int)(eDmg * ((Math.random() * 0.25) + 1));
        } // holy buffing the player, this is so much weaker than Player.attack() crits lmao
        target.takeDmg(eDmg);
        System.out.printf("%s attacks you for %d damage: you now have %d/%d hp\n", this, eDmg, target.getHP(), target.getMaxHP());
        return String.format("%s attacks %s for %d damage: %s now has %d/%d hp", this, target, eDmg, target, target.getHP(), target.getMaxHP());
    }
}
