/**
 * DeatherNote: unveil the secrets of the humble Deather
 */
public class DeatherNote extends Item {
    /**
     * create deatherNote
     */
    public DeatherNote () {
        super("Mysterious Note");
    }

    @Override
    public void use (Player target) {
        System.out.println("The note reads \"To use the Deather: speak thrice its name or face the consequences\"");
    }
}
