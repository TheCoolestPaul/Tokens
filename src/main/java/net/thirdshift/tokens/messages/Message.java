package net.thirdshift.tokens.messages;

import org.bukkit.ChatColor;

public class Message {

    private final String raw;
    private final String colorFormattedString;

    public Message(String stringIn){
        this.raw=stringIn;
        this.colorFormattedString= colorFormatted(stringIn);
    }

    public String colorFormatted(String str){
        return ChatColor.translateAlternateColorCodes('&',str);
    }

    public boolean isEmpty(){
        return this.colorFormattedString.isEmpty();
    }

    public String getRaw() {
        return raw;
    }
    
    public String getColorFormattedString() {
    	return colorFormattedString;
    }

}
