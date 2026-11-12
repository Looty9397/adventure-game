/**
 * Shovel: digging implement and deadly weapon
 */
public class Shovel extends Item {
    private final String type;

    public Shovel (String type, int atk) {
        super(type + " Shovel", true, atk);
        this.type = type;
    }

    @Override
    public void use (Player target) {
        System.out.println("Your " + this.getName() + " flashes in your hand");
    }

    @Override
    public Shovel copy () {
        return new Shovel(this.type, this.getATK());
    }
}
