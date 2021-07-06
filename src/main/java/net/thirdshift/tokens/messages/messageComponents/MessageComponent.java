package net.thirdshift.tokens.messages.messageComponents;

public abstract class MessageComponent {
    protected String replaces;

    public MessageComponent(String replaces) {
        this.replaces = replaces;
    }

    public abstract String apply(String message);
}
