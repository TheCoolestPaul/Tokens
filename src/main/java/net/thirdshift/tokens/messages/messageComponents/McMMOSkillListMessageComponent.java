package net.thirdshift.tokens.messages.messageComponents;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;

public class McMMOSkillListMessageComponent extends MessageComponent{
    private String list;
    public McMMOSkillListMessageComponent(SkillTools skillTools, PrimarySkillType[] skills) {
        super("%skill_list%");
        list="";
        for (PrimarySkillType skill: skills) {
            if (!SkillTools.isChildSkill(skill))
                list = list+skillTools.getLocalizedSkillName(skill)+",";
        }
    }

    @Override
    public String apply(String message) {
        return message.replaceAll(replaces, list);
    }
}
