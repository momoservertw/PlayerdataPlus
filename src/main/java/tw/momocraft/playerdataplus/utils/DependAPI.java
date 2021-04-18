package tw.momocraft.playerdataplus.utils;

import org.bukkit.Bukkit;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;

public class DependAPI {
    private boolean Vault = false;
    private boolean CMI = false;
    private boolean Residence = false;
    private boolean PlaceHolderAPI = false;
    private boolean MySQLPlayerDataBridge = false;
    private boolean SkinsRestorer = false;
    private boolean DiscordSRV = false;
    private boolean LuckPerms = false;
    private boolean MyPet = false;
    private boolean AuthMe = false;
    private boolean NameTagEdit = false;
    private boolean Essentials = false;
    private boolean MultiverseCore = false;
    private boolean PlayerPoints = false;
    private boolean MyCommand = false;

    public DependAPI() {
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Vault")) {
            this.setVaultStatus(Bukkit.getServer().getPlugin().getPlugin("Vault") != null);
            if (Vault) {
                setVaultApi();
            }
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.PlaceHolderAPI")) {
            this.setPlaceHolderStatus(Bukkit.getServer().getPlugin().getPlugin("PlaceHolderAPI") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Residence")) {
            this.setResidenceStatus(Bukkit.getServer().getPlugin().getPlugin("Residence") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.CMI")) {
            this.setCMIStatus(Bukkit.getServer().getPlugin().getPlugin("CMI") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MySQLPlayerDataBridge")) {
            this.setMySQLPlayerDataBridgeStatus(Bukkit.getServer().getPlugin().getPlugin("MySQLPlayerDataBridge") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.SkinsRestorer")) {
            this.setSkinsRestorerStatus(Bukkit.getServer().getPlugin().getPlugin("SkinsRestorer") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.DiscordSRV")) {
            this.setDiscordSRVStatus(Bukkit.getServer().getPlugin().getPlugin("DiscordSRV") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.LuckPerms")) {
            this.setLuckPermsStatus(Bukkit.getServer().getPlugin().getPlugin("LuckPerms") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MyPet")) {
            this.setMyPetStatus(Bukkit.getServer().getPlugin().getPlugin("MyPet") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.AuthMe")) {
            this.setAuthMeStatus(Bukkit.getServer().getPlugin().getPlugin("AuthMe") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.NameTagEdit")) {
            this.setNameTagEditStatus(Bukkit.getServer().getPlugin().getPlugin("NameTagEdit") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.Essentials")) {
            this.setEssentialsStatus(Bukkit.getServer().getPlugin().getPlugin("Essentials") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MultiverseCore")) {
            this.setMultiverseCoreStatus(Bukkit.getServer().getPlugin().getPlugin("MultiverseCore") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.PlayerPoints")) {
            this.setPlayerPointsStatus(Bukkit.getServer().getPlugin().getPlugin("PlayerPoints") != null);
        }
        if (ConfigHandler.getConfig("config.yml").getBoolean("General.Settings.Features.Hook.MyCommand")) {
            this.setMyCommandStatus(Bukkit.getServer().getPlugin().getPlugin("MyCommand") != null);
        }

        sendUtilityDepends();
    }

    private void sendUtilityDepends() {
        ServerHandler.sendConsoleMessage("&fHooked: "
                + (VaultEnabled() ? "Vault, " : "")
                + (CMIEnabled() ? "CMI, " : "")
                + (ResidenceEnabled() ? "Residence, " : "")
                + (PlaceHolderAPIEnabled() ? "PlaceHolderAPI, " : "")
                + (MySQLPlayerDataBridgeEnabled() ? "MySQLPlayerDataBridge, " : "")
                + (SkinsRestorerEnabled() ? "SkinsRestorer, " : "")
                + (DiscordSRVEnabled() ? "DiscordSRV, " : "")
                + (LuckPermsEnabled() ? "LuckPerms, " : "")
                + (MyPetEnabled() ? "MyPet, " : "")
                + (AuthMeEnabled() ? "Authme, " : "")
                + (EssentialsEnabled() ? "Essentials," : "")
                + (MultiverseCoreEnabled() ? "MultiverseCore," : "")
                + (PlayerPointsEnabled() ? "PlayerPoints," : "")
                + (MyCommandEnabled() ? "MyCommand," : "")
        );
    }

    public boolean VaultEnabled() {
        return this.Vault;
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
        return this.NameTagEdit;
    }

    public boolean EssentialsEnabled() {
        return this.Essentials;
    }

    public boolean MultiverseCoreEnabled() {
        return this.MultiverseCore;
    }

    public boolean PlayerPointsEnabled() {
        return this.PlayerPoints;
    }

    public boolean MyCommandEnabled() {
        return this.MyCommand;
    }


    public void setVaultStatus(boolean bool) {
        this.Vault = bool;
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

    private void setDiscordSRVStatus(boolean bool) {
        this.DiscordSRV = bool;
    }

    private void setLuckPermsStatus(boolean bool) {
        this.LuckPerms = bool;
    }

    private void setMyPetStatus(boolean bool) { this.MyPet = bool; }

    private void setAuthMeStatus(boolean bool) {
        this.AuthMe = bool;
    }

    private void setNameTagEditStatus(boolean bool) { this.NameTagEdit = bool; }

    private void setEssentialsStatus(boolean bool) { this.Essentials = bool; }

    private void setMultiverseCoreStatus(boolean bool) { this.MultiverseCore = bool; }

    private void setPlayerPointsStatus(boolean bool) { this.PlayerPoints = bool; }

    private void setMyCommandStatus(boolean bool) { this.MyCommand = bool; }
}
