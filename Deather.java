/**
 * Deather: speak thrice its name and thou shalt rain hellfyre upon thy foes
 */
public class Deather extends Item {
    private boolean success;

    public Deather () {
        super("Deather", false, true, 1);
    }

    @Override
    public void use (Player target) {
        // This method alone is the reason I have had to add Input.java
        // And yes, there is no prompt. Good Luck!
        // the only tutorial is through the DeatherNote
        if (Input.next().equals("deather")) {
            if (Input.next().equals("deather")) {
                // no need for a third if
                success = Input.next().equals("deather");
            } else {
                success = false;
            }
        } else {
            success = false;
        }
    }

    @Override
    public int getATK () {
        // if success, then op hit
        return super.getATK() + ((success) ? 50 : 0);
    }
}
