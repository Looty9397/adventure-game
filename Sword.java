/**
 * Sword: melee weapon
 */
public class Sword extends Item {
    public Sword (String type, int atk) {
        super(type + " Sword", false, true, atk);
    }

    @Override
    public void use (Player target) {
        System.out.println("You swing your " + this.getName() + " with fury");
    }
}
