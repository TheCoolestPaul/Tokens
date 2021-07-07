package net.thirdshift.tokens.messages.messageComponents;

public class CombatMessageComponent extends MessageComponent{
    private final int secondsLeft;

    public CombatMessageComponent(int secondsLeft) {
        super("%seconds%");
        this.secondsLeft = secondsLeft;
    }

    @Override
    public String apply(String message) {
        return message.replaceAll("%seconds%", String.valueOf(secondsLeft));
    }
}
