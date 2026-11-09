public class DeatherNote extends Item {
    public DeatherNote () {
        super("Mysterious Note");
    }

    @Override
    public void use (Player target) {
        System.out.println("Speak thrice the name 'Deather' to invoke limitless suffering upon thy foes.");
    }
}
