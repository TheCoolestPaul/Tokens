package main.java;

import java.net.UnknownHostException;

import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDB {
	public MongoDB() {
	}
	private DBCollection players;
    private DB mcserverdb;
    private MongoClient client;
    public boolean connect(String ip, int port){
        try {
            client = new MongoClient(ip, port);
        } catch (UnknownHostException e) {
            System.out.println("Could not connect to database!");
            e.printStackTrace();
            return false;
        }
        mcserverdb = client.getDB("tokens");
        players = mcserverdb.getCollection("players");
        return true;
    }
	public void storePlayer(Player player, String name, long tokens){
        DBObject obj = new BasicDBObject("uuid", player.getUniqueId());
        obj.put("name", name);
        obj.put("tokens", tokens);
        players.insert(obj);
    }
    public long getPlayerTokens(Player player){
        DBObject r = new BasicDBObject("uuid", player.getUniqueId());
        DBObject found = players.findOne(r);
        if(found==null){
        	storePlayer(player, player.getName(), 0);
            return 0;
        }
        long tokens = (long) found.get("tokens");
        return tokens;
    }
    public void pluginDisableSavePlayers(Player player, long tokens) {
        DBObject r = new BasicDBObject("uuid", player.getUniqueId());
        DBObject found = players.findOne(r);
        if (found == null){
        	storePlayer(player, player.getName(), 0);
            return;
        }
        BasicDBObject set = new BasicDBObject("$set", r);
        set.append("$set", new BasicDBObject("tokens", tokens));
        players.update(found, set);
        System.out.println("Saved player "+player.getName());
    }
}
