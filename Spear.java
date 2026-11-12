/**
 * Spear: melee weapon
 */
public class Spear extends Item {
    private final String type;

    public Spear (String type, int atk) {
        super(type + " Spear", true, atk);
        this.type = type;
    }

    @Override
    public void use (Player target) {
        System.out.println("You thrust your " + this.getName() + " at your opponent");
    }

    @Override
    public Spear copy () {
        return new Spear(this.type, this.getATK());
    }
}
