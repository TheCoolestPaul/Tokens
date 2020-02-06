package net.thirdshift.tokens.keys;

public class Key {

    public String keyString;
    public boolean enabled;
    public boolean oneTime;
    public int tokens;
    public double cooldown;

    public Key(String keyString, boolean enabled, boolean oneTime, int tokens, double cooldown){
        this.keyString=keyString;
        this.enabled=enabled;
        this.oneTime=oneTime;
        this.tokens=tokens;
        this.cooldown=cooldown;
    }

}
