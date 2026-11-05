public class Character {
    private int hp;
    private int maxHP;
    private int str;
    private String name;
    private String speech;

    public Character (String name, int maxHP, int str) {
        this(name, maxHP, str, "");
    }

    public Character (String name, int maxHP, int str, String speech) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = this.maxHP;
        this.speech = speech;
        this.str = str;
    }

    public void introduce () {
        System.out.println(this.speech);
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

    public void setHP (int val) {
        if (val > this.maxHP) {
            this.hp = this.maxHP;
        } else {
            this.hp = val;
        }
    }

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
}
