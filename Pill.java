/**
 * Pill: 30/70 kill/save
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
            // ~~not much but it's enough~~
            // needs to be plenty so that the reward justifies the risk
            int healAmt = (int)(Math.random() * 11) + 20; // 20 - 30
            target.heal(healAmt);
            System.out.println("You swallowed the pill and healed " + healAmt + " hp");
        } else {
            target.setHP(1);
            System.out.println("You swallowed the pill and almost died");
            // unfortunately can't seem to make this also print to the log file, too bad so sad
        }
    }
}
