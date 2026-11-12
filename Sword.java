/**
 * Sword: melee weapon
 */
public class Sword extends Item {
    private final String type;

    public Sword (String type, int atk) {
        super(type + " Sword", true, atk);
        this.type = type;
    }

    @Override
    public void use (Player target) {
        System.out.println("You swing your " + this.getName() + " with fury");
    }

    // Each weapon needs this to preserve the overrides when equipping
    @Override
    public Sword copy () {
        return new Sword(this.type, this.getATK());
    }
}
