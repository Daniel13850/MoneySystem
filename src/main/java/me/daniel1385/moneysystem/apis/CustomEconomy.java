package me.daniel1385.moneysystem.apis;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CustomEconomy implements Economy {
    private MySQL mysql;

    public CustomEconomy(MySQL mysql) {
        this.mysql = mysql;
    }

    @Override
    public EconomyResponse bankBalance(String arg0) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankDeposit(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankHas(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String arg0, double arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String arg0, String arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public boolean createPlayerAccount(String arg0) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String arg0, String arg1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return "$";
    }

    @Override
    public String currencyNameSingular() {
        return "$";
    }

    @Override
    public EconomyResponse deleteBank(String arg0) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse depositPlayer(String arg0, double arg1) {
        UUID uuid = PlayerNameAPI.getUUID(arg0);
        if(uuid == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player not exist!");
        }
        return depositPlayer(uuid, arg1, "Vault");
    }

    public EconomyResponse depositPlayer(UUID uuid, double val, String reason) {
        try {
            double bal = mysql.getMoney(uuid);
            if(val < 0) {
                return new EconomyResponse(0, bal, ResponseType.FAILURE, "Invalid Number!");
            }
            Player p = Bukkit.getPlayer(uuid);
            String display = null;
            if(p != null) {
                display = p.getDisplayName();
            }
            mysql.addMoney(uuid, val, reason, display);
            return new EconomyResponse(val, bal + val, ResponseType.SUCCESS, null);
        } catch (SQLException e) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "MySQL Error!");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer arg0, double arg1) {
        UUID uuid = arg0.getUniqueId();
        return depositPlayer(uuid, arg1, "Vault");
    }

    @Override
    public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer arg0, String arg1, double arg2) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public String format(double arg0) {
        return DecimalFormat.getNumberInstance(Locale.GERMAN).format(arg0) + currencyNamePlural();
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public double getBalance(String arg0) {
        UUID uuid = PlayerNameAPI.getUUID(arg0);
        if(uuid == null) {
            return 0;
        }
        return getBalance(uuid);
    }

    public double getBalance(UUID uuid) {
        try {
            return mysql.getMoney(uuid);
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public double getBalance(OfflinePlayer arg0) {
        UUID uuid = arg0.getUniqueId();
        return getBalance(uuid);
    }

    @Override
    public double getBalance(String arg0, String arg1) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer arg0, String arg1) {
        return 0;
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return "MoneySystem";
    }

    @Override
    public boolean has(String arg0, double arg1) {
        UUID uuid = PlayerNameAPI.getUUID(arg0);
        if(uuid == null) {
            return false;
        }
        return has(uuid, arg1);
    }

    public boolean has(UUID uuid, double arg1) {
        try {
            return mysql.hasEnough(uuid, arg1);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean has(OfflinePlayer arg0, double arg1) {
        UUID uuid = arg0.getUniqueId();
        return has(uuid, arg1);
    }

    @Override
    public boolean has(String arg0, String arg1, double arg2) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
        return false;
    }

    @Override
    public boolean hasAccount(String arg0) {
        UUID uuid = PlayerNameAPI.getUUID(arg0);
        if(uuid == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer arg0) {
        return true;
    }

    @Override
    public boolean hasAccount(String arg0, String arg1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer arg0, String arg1) {
        return true;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse isBankMember(String arg0, String arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, String arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public EconomyResponse withdrawPlayer(String arg0, double arg1) {
        UUID uuid = PlayerNameAPI.getUUID(arg0);
        if(uuid == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player not exist!");
        }
        return withdrawPlayer(uuid, arg1, "Vault");
    }

    public EconomyResponse withdrawPlayer(UUID uuid, double val, String reason) {
        try {
            double bal = mysql.getMoney(uuid);
            if(val < 0) {
                return new EconomyResponse(0, bal, ResponseType.FAILURE, "Invalid Number!");
            }
            Player p = Bukkit.getPlayer(uuid);
            String display = null;
            if(p != null) {
                display = p.getDisplayName();
            }
            if(!mysql.removeMoney(uuid, val, reason, display)) {
                return new EconomyResponse(0, bal, ResponseType.FAILURE, "Not enough Money!");
            } else {
                return new EconomyResponse(-val, bal - val, ResponseType.SUCCESS, null);
            }
        } catch (SQLException e) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "MySQL Error!");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer arg0, double arg1) {
        UUID uuid = arg0.getUniqueId();
        return withdrawPlayer(uuid, arg1, "Vault");
    }

    @Override
    public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
    }

}
