package net.thirdshift.tokens.messages.messageComponents;

public class TokensMessageComponent extends MessageComponent{
    int num;

    public TokensMessageComponent(int numOfTokens) {
        super("%tokens%");
        num=numOfTokens;
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, String.valueOf(num));
    }
}
