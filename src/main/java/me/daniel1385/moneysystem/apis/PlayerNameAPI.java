package me.daniel1385.moneysystem.apis;

import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class PlayerNameAPI {

    public static UUID getUUID(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if(p != null) {
            return p.getUniqueId();
        }
        UUID uuid = null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("floodgate");
        if(plugin != null) {
            uuid = FloodgateAPI.getUUID(name);
        }
        if(uuid == null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader((new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).openStream()));
                String result = JsonParser.parseReader(in).getAsJsonObject().get("id").toString().replaceAll("\"", "");
                result = result.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
                uuid = UUID.fromString(result);
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            } catch (Exception e) {}
        }
        return uuid;
    }

    public static String getName(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p != null) {
            return p.getName();
        }
        String name = null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("floodgate");
        if(plugin != null) {
            name = FloodgateAPI.getName(uuid);
        }
        if(name == null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader((new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).openStream()));
                name = JsonParser.parseReader(in).getAsJsonObject().get("name").toString().replaceAll("\"", "");
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            } catch (Exception e) {}
        }
        return name;
    }

}
