package net.thirdshift.tokens.messages.messageComponents;

public class McMMOSkillMessageComponent extends MessageComponent{
    private final String skillName;

    public McMMOSkillMessageComponent(String skillName) {
        super("%skill_name%");
        this.skillName = skillName;
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, skillName);
    }
}
