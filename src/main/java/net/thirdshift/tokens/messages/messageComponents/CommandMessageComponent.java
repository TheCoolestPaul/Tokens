package net.thirdshift.tokens.messages.messageComponents;

public class CommandMessageComponent extends MessageComponent{
    private final String usage;
    public CommandMessageComponent(String commandUsage) {
        super("%command_usage%");
        this.usage = commandUsage;
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, usage);
    }
}
