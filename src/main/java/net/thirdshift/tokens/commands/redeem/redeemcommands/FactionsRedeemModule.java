package net.thirdshift.tokens.commands.redeem.redeemcommands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import net.thirdshift.tokens.messages.messageData.PlayerSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionsRedeemModule extends RedeemModule {
    public FactionsRedeemModule(){
        super();
    }

    @Override
    public String getCommand() {
        return "factions";
    }

    @Override
    public String[] getCommandAliases() {
        return new String[]{"faction", "f"};
    }

    @Override
    public String getCommandUsage() {
        return "/redeem factions <tokens to spend>";
    }

    @Override
    public void onCommand(final CommandSender commandSender, final ArrayList<String> args) {
        Player player = (Player) commandSender;
        List<Object> objects = new ArrayList<>();
        if (args.size()!=1){
            objects.add(new PlayerSender(player));
            objects.add(getCommandUsage());
            player.sendMessage(plugin.messageHandler.useMessage("tokens.errors.invalid-command.correction", objects));
            return;
        }

        int toRedeem;
        try {
            toRedeem = Integer.parseInt(args.get(0));
        }catch(NumberFormatException e){
            player.sendMessage(ChatColor.RED +"Invalid command, "+args.get(0)+" is not a number!");
            return;
        }

        objects.add(toRedeem);
        objects.add(new PlayerSender(player));

        if(tokensHandler.hasTokens(player, toRedeem)){
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if(fPlayer != null ){
                fPlayer.setPowerBoost(fPlayer.getPowerBoost() + (double)(toRedeem * plugin.getTokensConfigHandler().getTokenToFactionPower()));
                objects.add(fPlayer);
                tokensHandler.setTokens(player, tokensHandler.getTokens(player) - toRedeem);
                player.sendMessage(plugin.messageHandler.useMessage("redeem.factions", objects));
            }else{
                plugin.getLogger().severe("Couldn't get FPlayer for "+player.getName());
            }
        }else{
            player.sendMessage(plugin.messageHandler.useMessage("redeem.errors.not-enough", objects));
        }
    }
}
