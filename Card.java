/**
 * Card: manipulate the player's money
 */
public class Card extends Item {
    private final int value;

    public Card (int value) {
        super("Credit Card", true);
        this.value = value;
    }

    @Override
    public void use (Player target) {
        if (this.value >= 0) {
            target.addMoney(this.value);
            System.out.println("You used the credit card and got $" + this.value);
        } else {
            target.loseMoney(-this.value);
            System.out.println("You used the credit card and lost $" + -this.value);
        }
    }
}
