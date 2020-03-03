package tw.momocraft.playerdataplus.utils;

import org.bukkit.Bukkit;

public class DependAPI {
    private boolean CMI = false;
    private boolean Residence = false;
    private boolean PlaceHolderAPI = false;
    private boolean MySQLPlayerDataBridge = false;
    private boolean SkinsRestorer = false;
    private boolean ChatControlPro = false;
    private boolean DiscordSRV = false;
    private boolean LuckPerms = false;
    private boolean MyPet = false;
    private boolean AuthMe = false;
    private boolean NameTagEdit = false;
    private VaultAPI vault;

    public DependAPI() {
        this.setCMIStatus(Bukkit.getServer().getPluginManager().getPlugin("CMI") != null);
        this.setResidenceStatus(Bukkit.getServer().getPluginManager().getPlugin("Residence") != null);
        this.setPlaceHolderStatus(Bukkit.getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null);
        this.setMySQLPlayerDataBridgeStatus(Bukkit.getServer().getPluginManager().getPlugin("MySQLPlayerDataBridge") != null);
        this.setSkinsRestorerStatus(Bukkit.getServer().getPluginManager().getPlugin("SkinsRestorer") != null);
        this.setChatControlProStatus(Bukkit.getServer().getPluginManager().getPlugin("ChatControlPro") != null);
        this.setDiscordSRVStatus(Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null);
        this.setLuckPermsStatus(Bukkit.getServer().getPluginManager().getPlugin("LuckPerms") != null);
        this.setMyPetStatus(Bukkit.getServer().getPluginManager().getPlugin("MyPet") != null);
        this.setAuthMeStatus(Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null);
        this.setNameTagEditStatus(Bukkit.getServer().getPluginManager().getPlugin("NameTagEdit") != null);
        this.setVault();
    }


    public boolean CMIEnabled() {
        return this.CMI;
    }

    public boolean ResidenceEnabled() {
        return this.Residence;
    }

    public boolean PlaceHolderAPIEnabled() {
        return this.PlaceHolderAPI;
    }

    public boolean MySQLPlayerDataBridgeEnabled() {
        return this.MySQLPlayerDataBridge;
    }

    public boolean SkinsRestorerEnabled() {
        return this.SkinsRestorer;
    }

    public boolean ChatControlProEnabled() {
        return this.ChatControlPro;
    }

    public boolean DiscordSRVEnabled() {
        return this.DiscordSRV;
    }

    public boolean LuckPermsEnabled() {
        return this.LuckPerms;
    }

    public boolean MyPetEnabled() {
        return this.MyPet;
    }

    public boolean AuthMeEnabled() {
        return this.AuthMe;
    }

    public boolean NameTagEditEnabled() {
        return this.AuthMe;
    }


    private void setCMIStatus(boolean bool) {
        this.CMI = bool;
    }

    private void setResidenceStatus(boolean bool) {
        this.Residence = bool;
    }

    private void setPlaceHolderStatus(boolean bool) {
        this.PlaceHolderAPI = bool;
    }

    private void setMySQLPlayerDataBridgeStatus(boolean bool) {
        this.MySQLPlayerDataBridge = bool;
    }

    private void setSkinsRestorerStatus(boolean bool) {
        this.SkinsRestorer = bool;
    }

    private void setChatControlProStatus(boolean bool) {
        this.ChatControlPro = bool;
    }

    private void setDiscordSRVStatus(boolean bool) {
        this.DiscordSRV = bool;
    }

    private void setLuckPermsStatus(boolean bool) {
        this.LuckPerms = bool;
    }

    private void setMyPetStatus(boolean bool) { this.MyPet = bool; }

    private void setNameTagEditStatus(boolean bool) { this.MyPet = bool; }

    private void setAuthMeStatus(boolean bool) {
        this.AuthMe = bool;
    }

    public VaultAPI getVault() {
        return this.vault;
    }

    private void setVault() {
        this.vault = new VaultAPI();
    }
}
