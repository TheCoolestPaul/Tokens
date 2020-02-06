package net.thirdshift.tokens.keys;

public class Key {

    public String keyString;
    public boolean enabled;
    public boolean oneTime;
    public int tokens;
    public double cooldown;

    public Key(String keyString){
        this.keyString=keyString;
    }

    public Key(String keyString, boolean enabled, boolean oneTime, int tokens, double cooldown){
        this.keyString=keyString;
        this.enabled=enabled;
        this.oneTime=oneTime;
        this.tokens=tokens;
        this.cooldown=cooldown;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public String toString() {
        return "Key{" +
                "keyString='" + keyString + '\'' +
                ", enabled=" + enabled +
                ", oneTime=" + oneTime +
                ", tokens=" + tokens +
                ", cooldown=" + cooldown +
                '}';
    }
}
