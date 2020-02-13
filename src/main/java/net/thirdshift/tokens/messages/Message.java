package net.thirdshift.tokens.messages;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.thirdshift.tokens.messages.playerTypes.PlayerSender;
import net.thirdshift.tokens.messages.playerTypes.PlayerTarget;
import org.bukkit.ChatColor;

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
                ret = ret.replace("%sender%", ((PlayerSender) obj).player);
            }
            if(obj instanceof PlayerTarget) {
                ret = ret.replace("%target%", ((PlayerTarget) obj).player);
            }
            if(obj instanceof Integer){
                ret = ret.replace("%tokens%", String.valueOf(obj));
            }
            if (obj instanceof PrimarySkillType){
                ret = ret.replace("%skillName%", ((PrimarySkillType) obj).getName());
            }
            if(obj instanceof String){
                ret = ret.replace("%command_usage%", obj.toString());
            }
        }
        return ret;
    }

    public boolean isEmpty(){
        return this.formatted.isEmpty();
    }

    public String getRaw() {
        return raw;
    }

}
