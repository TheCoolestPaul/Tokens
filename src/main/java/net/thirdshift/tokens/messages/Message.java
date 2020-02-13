package net.thirdshift.tokens.messages;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.thirdshift.tokens.messages.playerTypes.PlayerSender;
import net.thirdshift.tokens.messages.playerTypes.PlayerTarget;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {

    private String raw;
    private String formatted;

    public Message(String stringIn){
        this.raw=stringIn;
        this.formatted=formatString(stringIn);
    }

    public String formatString(String str){
        return ChatColor.translateAlternateColorCodes('&',str);
    }

    public String use(List<Object> objects){
        String ret = formatted;
        for(Object obj : objects) {
            if (obj instanceof PlayerSender) {
                ret = formatted.replace("%sender%", ((Player) obj).getDisplayName());
            }else if(obj instanceof PlayerTarget) {
                ret = formatted.replace("%target%", ((PlayerTarget) obj).getDisplayName());
            }else if(obj instanceof Integer){
                ret = formatted.replace("%tokens%", String.valueOf(obj));
            }else if (obj instanceof PrimarySkillType){
                ret = formatted.replace("%skillName%", ((PrimarySkillType) obj).getName());
            }
        }
        return ret;
    }

    public String getRaw() {
        return raw;
    }

}
