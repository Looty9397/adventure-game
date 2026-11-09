/**
 * Spear: melee weapon
 */
public class Spear extends Item {
    public Spear (String type, int atk) {
        super(type + " Spear", false, true, atk);
    }

    @Override
    public void use (Player target) {
        System.out.println("You thrust your " + this.getName() + " at your opponent");
    }
}
