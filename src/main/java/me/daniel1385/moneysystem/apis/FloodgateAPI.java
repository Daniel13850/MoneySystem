package me.daniel1385.moneysystem.apis;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateAPI {

    public static UUID getUUID(String name) {
        try {
            String prefix = FloodgateApi.getInstance().getPlayerPrefix();
            if(name.startsWith(prefix)) {
                Long xuid = FloodgateApi.getInstance().getXuidFor(name.substring(prefix.length())).get();
                if(xuid != null) {
                    return FloodgateApi.getInstance().createJavaPlayerId(xuid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getName(UUID uuid) {
        try {
            if(FloodgateApi.getInstance().isFloodgateId(uuid)) {
                String result = FloodgateApi.getInstance().getGamertagFor(uuid.getLeastSignificantBits()).get();
                if(result != null) {
                    return FloodgateApi.getInstance().getPlayerPrefix() + result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
