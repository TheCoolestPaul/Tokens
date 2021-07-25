package net.thirdshift.tokens.messages.messageComponents;

public class MoneyMessageComponent extends MessageComponent {
    private final String money;
    public MoneyMessageComponent(double money) {
        super("%money%");
        this.money=String.valueOf(money);
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces,money);
    }
}
