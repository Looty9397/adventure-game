/**
 * Pill: 30/70 kill/heal
 */
public class Pill extends Item {
    private final boolean seed;

    public Pill () {
        super("Pill", true);
        this.seed = Math.random() > 0.3;
    }

    @Override
    public void use (Player target) {
        if (seed) {
            // not much but it's enough
            int healAmt = (int)(Math.random() * 10) + 5;
            target.heal(healAmt);
            System.out.println("You swallowed the pill and healed " + healAmt + " hp");
        } else {
            target.setHP(0);
            System.out.println("You swallowed the pill and immediately died");
        }
    }
}
