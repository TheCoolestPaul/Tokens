package net.thirdshift.tokens.messages;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.massivecraft.factions.FPlayer;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import net.thirdshift.tokens.messages.messageData.PlayerTarget;
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
        if(this.isEmpty()){
            return "";
        }
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
                ret = ret.replace("%skill_name%", ((PrimarySkillType) obj).getName());
            }
            if (obj instanceof List){
                ret = ret.replace("%skill_list%", obj.toString());
            }
            if(obj instanceof String){
                ret = ret.replace("%command_usage%", obj.toString());
            }
            if(obj instanceof Double){
                ret = ret.replace("%money%", obj.toString());
            }
            /*if(obj instanceof FPlayer){
                ret = ret.replace("%power%", String.valueOf(((FPlayer) obj).getPowerMax()));
            }*/
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
