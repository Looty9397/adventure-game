/**
 * Shovel: digging implement and deadly weapon
 */
public class Shovel extends Item {
    public Shovel (String type, int atk) {
        super(type + " Shovel", false, true, atk);
    }

    @Override
    public void use (Player target) {
        System.out.println("Your " + this.getName() + " flashes in your hand");
    }
}
