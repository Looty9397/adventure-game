/**
 * Food: heal by a small amount
 */
public class Food extends Item {
    private final int sat; // sat = saturation

    /**
     * create a food
     * @param name
     * @param sat
     */
    public Food (String name, int sat) {
        super(name, true);
        this.sat = sat;
    }

    @Override
    public void use (Player target) {
        target.heal(sat * 2);
        System.out.println("You ate " + this.getName() + " and healed " + (sat * 2) + " hp");
    }
}
