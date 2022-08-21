package tw.momocraft.playerdataplus;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.coreplus.utils.file.maps.LogMap;
import tw.momocraft.playerdataplus.handlers.ConfigHandler;
import tw.momocraft.playerdataplus.handlers.UtilsHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserConvertor {

    private Table<String, String, String> oldUserTable;
    private Table<String, String, String> newUserTable;

    private void setup() {
        File file = new File(Bukkit.getWorldContainer().getPath() +
                "\\plugins\\PlayerdataPlus\\logs\\UserConvertor", "latest.log");
        LogMap logMap = new LogMap();
        logMap.setFile(file);
        logMap.setGroupName("UserConvertor");
        logMap.setNewDateFile(true);
        CorePlusAPI.getFile().getLog().load("PlayerdataPlus-UserConvertor", logMap);
    }

    private void check(String oldName, String newName) {
        UUID newUUID = CorePlusAPI.getPlayer().getPlayerUUID(oldName);
        UUID oldUUID = CorePlusAPI.getPlayer().getPlayerUUID(newName);
        if (CorePlusAPI.getPlayer().getOfflinePlayer(oldUUID).hasPlayedBefore()) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Player hasn't played before: " + oldName + ", " + oldUUID);
            return;
        }
        if (CorePlusAPI.getPlayer().getOfflinePlayer(oldUUID).hasPlayedBefore()) {
            CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                    "Player hasn't played before: name = " + newName + ", " + newUUID);
            return;
        }
        oldUserTable = HashBasedTable.create();
        // Log the settings.
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "░░░░░░░░░░ User-Convertor Logs - " + date + " ░░░░░░░░░░");
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "Action: Check");
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "--- Data ---");
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "CMI: " + ConfigHandler.getConfigPath().isUserConvertorCMI());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "Residence: " + ConfigHandler.getConfigPath().isUserConvertorResidence());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "MyPet: " + ConfigHandler.getConfigPath().isUserConvertorMyPet());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "MySuite: " + ConfigHandler.getConfigPath().isUserConvertorMySuite());
        CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                "UserConvertor", "LuckPerms: " + ConfigHandler.getConfigPath().isUserConvertorLuckPerms());

        checkCMI(oldUUID, newUUID);
        checkPoints(oldUUID, newUUID);
        //checkMyPet(oldUUID, newUUID);
        //checkMySuite(oldUUID, newUUID);
        checkResidence(oldUUID, newUUID);
        checkLuckPerms(oldUUID, newUUID);
    }

    private void checkCMI(UUID oldUUID, UUID newUUID) {
        if (!UtilsHandler.getDepend().CMIEnabled())
            return;
        // Get user data.
        CMIUser oldUser;
        CMIUser newUser;
        try {
            oldUser = CMI.getInstance().getPlayerManager().getUser(oldUUID);
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not find CMI user \"" + oldUUID + "\"!");
            return;
        }
        try {
            newUser = CMI.getInstance().getPlayerManager().getUser(newUUID);
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not find CMI user \"" + newUUID + "\"!");
            return;
        }
        // Money
        try {
            oldUserTable.put("CMI", "money", String.valueOf(oldUser.getEconomyAccount().getBalance()));
            newUserTable.put("CMI", "money", String.valueOf(newUser.getEconomyAccount().getBalance()));
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not get the CMI money: \"" + oldUUID + "\" -> \"" + newUUID + "\"");
            CorePlusAPI.getFile().getLog().add(ConfigHandler.getPluginName(),
                    "UserConvertor", "&cCan not get the CMI money: \"" + oldUUID + "\" -> \"" + newUUID + "\"");
            return;
        }
        // Nick
        try {
            oldUserTable.put("CMI", "nickname", oldUser.getNickName());
            newUserTable.put("CMI", "nickname", newUser.getNickName());
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not convert CMI nick \"" + oldUUID + "\" -> \"" + newUUID + "\"");
        }
        // Home
        try {
            Map<String, Location> map = CorePlusAPI.getPlayer().getCMIHomes(oldUUID);
            Location location;
            for (Map.Entry<String, Location> entry : map.entrySet()) {
                location = entry.getValue();
                oldUserTable.put("CMI", "home",
                        entry.getKey() + " - " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            }
            map = CorePlusAPI.getPlayer().getCMIHomes(newUUID);
            for (Map.Entry<String, Location> entry : map.entrySet()) {
                location = entry.getValue();
                newUserTable.put("CMI", "home",
                        entry.getKey() + " - " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            }
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not convert CMI money \"" + oldUUID + "\" -> \"" + newUUID + "\"");
        }
        // Inventory
        // Armor(36, 39), Helmet(39), ChestPlate(38), Pants(37), Boots(36), OffHand(40), MainHand(-1),
        // QuickBar(0, 8), PartInventory(9, 35), MainInventory(0, 35), CraftingIngredients(1, 4), CraftingResult(0);
        CMIPlayerInventory cmiPlayerInventory = oldUser.getInventory();
        List<ItemStack> itemStackList;
        String types = "Armor, Helmet, ChestPlate, Pants, Boots, OffHand, MainHand, QuickBar, " +
                "PartInventory, MainInventory, CraftingIngredients, CraftingResult";
        List<String> typeList = new ArrayList<>(Arrays.asList(types.split(", ")));
        for (String type : typeList) {
            itemStackList = cmiPlayerInventory.getItems(CMIPlayerInventory.CMIInventorySlot.valueOf(type));
            for (ItemStack itemStack : itemStackList) {
                newUserTable.put("CMI",
                        "inventory - " + type, itemStack.getItemMeta().getDisplayName());
            }
        }
    }

    private void checkPoints(UUID oldUUID, UUID newUUID) {
        if (!CorePlusAPI.getDepend().PlayerPointsEnabled())
            return;
        double oldPoints = CorePlusAPI.getPlayer().getPlayerPoints(ConfigHandler.getPluginName(), oldUUID);
        double newPoints = CorePlusAPI.getPlayer().getPlayerPoints(ConfigHandler.getPluginName(), newUUID);
        oldUserTable.put("PlayerPoints", "points", String.valueOf(oldPoints));
        newUserTable.put("PlayerPoints", "points", String.valueOf(newPoints));
    }

        /*
    private void checkMyPet(UUID oldUUID, UUID newUUID) {
        if (!CorePlusAPI.getDepend().MyPetEnabled())
            return;
        UUID uuid = MyPetApi.getPlayerManager().getInternalUUID(oldUUID);
        MyPetPlayer myPetPlayer = MyPetApi.getPlayerManager().getMyPetPlayer(uuid);
        MyPetApi.getMyPetManager().
        oldUserTable.put("PlayerPoints","points", String.valueOf(oldPoints));
        newUserTable.put("PlayerPoints","points", String.valueOf(newPoints));
    }
         */

    private void checkResidence(UUID oldUUID, UUID newUUID) {
        if (!UtilsHandler.getDepend().ResidenceEnabled())
            return;
        ResidencePlayer oldPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(oldUUID);
        ResidencePlayer newPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(newUUID);
        for (ClaimedResidence residence : oldPlayer.getResList()) {
            oldUserTable.put("Residence", "residences", residence.getResidenceName());
        }
        for (ClaimedResidence residence : newPlayer.getResList()) {
            newUserTable.put("Residence", "residences", residence.getResidenceName());
        }
    }

    private void checkLuckPerms(UUID oldUUID, UUID newUUID) {
        if (CorePlusAPI.getDepend().LuckPermsEnabled())
            return;
        for (String group : CorePlusAPI.getPlayer().getLuckPermsInheritedGroups(oldUUID))
            oldUserTable.put("LuckPerms", "groups", group);
        for (String permission : CorePlusAPI.getPlayer().getLuckPermsAllPerms(oldUUID))
            oldUserTable.put("LuckPerms", "permissions", permission);
        for (String group : CorePlusAPI.getPlayer().getLuckPermsInheritedGroups(newUUID))
            newUserTable.put("LuckPerms", "groups", group);
        for (String permission : CorePlusAPI.getPlayer().getLuckPermsAllPerms(newUUID))
            newUserTable.put("LuckPerms", "permissions", permission);
    }
}


    /*
    private void copyCMI(Player sender, UUID oldUUID, UUID newUUID) {
        if (!UtilsHandler.getDepend().CMIEnabled())
            return;
        CMIUser oldUser;
        CMIUser newUser;
        try {
            oldUser = CMI.getInstance().getPlayerManager().getUser(oldUUID);
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not find CMI user \"" + oldUUID + "\"!");
            return;
        }
        try {
            newUser = CMI.getInstance().getPlayerManager().getUser(newUUID);
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not find CMI user \"" + newUUID + "\"!");
            return;
        }
        // Money
        try {
            newUser.getEconomyAccount().setBalance(oldUser.getEconomyAccount().getBalance());
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not convert CMI money \"" + oldUUID + "\" -> \"" + newUUID + "\"");
        }
        // Nick
        try {
            newUser.setName(oldUser.getNickName());
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not convert CMI nick \"" + oldUUID + "\" -> \"" + newUUID + "\"");
        }
        // Home
        try {
            for (Map.Entry<String, CmiHome> entry : oldUser.getHomes().entrySet())
                newUser.addHome(entry.getValue(), true);
        } catch (Exception e) {
            CorePlusAPI.getMsg().sendConsoleMsg(ConfigHandler.getPluginName(),
                    "&cCan not convert CMI money \"" + oldUUID + "\" -> \"" + newUUID + "\"");
        }
        // Inventory
         Armor(36, 39), Helmet(39), ChestPlate(38), Pants(37), Boots(36), OffHand(40), MainHand(-1),
           QuickBar(0, 8), PartInventory(9, 35), MainInventory(0, 35), CraftingIngredients(1, 4), CraftingResult(0);

        CMIPlayerInventory cmiPlayerInventory = oldUser.getInventory();
        ItemStack itemStack;
        BlockStateMeta blockStateMeta;
        ShulkerBox shulker;
        List<ItemStack> itemStackList;
        String types = "Armor, Helmet, ChestPlate, Pants, Boots, OffHand, MainHand, QuickBar, PartInventory, MainInventory, CraftingIngredients, CraftingResult";
        List<String> typeList = new ArrayList<>(Arrays.asList(types.split(", ")));
        for (String type : typeList) {
            itemStackList = cmiPlayerInventory.getItems(CMIPlayerInventory.CMIInventorySlot.valueOf(type));
            // Create a shulker box.
            itemStack = new ItemStack(Material.SHULKER_BOX);
            blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
            shulker = (ShulkerBox) blockStateMeta.getBlockState();
            // Import the items.
            shulker.getInventory().setContents((ItemStack[]) itemStackList.toArray());
            blockStateMeta.setBlockState(shulker);
            shulker.update();
            itemStack.setItemMeta(blockStateMeta);
            // Give sender the shulker box.
            sender.getInventory().setItem(CorePlusAPI.getPlayer().getInventoryEmptySlot(sender), itemStack);
        }
    }

    private void copyPoints(UUID oldUUID, UUID newUUID) {
        if (!CorePlusAPI.getDepend().PlayerPointsEnabled())
            return;
        double oldPoints = CorePlusAPI.getPlayer().getPlayerPoints(ConfigHandler.getPluginName(), oldUUID);
        CorePlusAPI.getPlayer().setPoints(ConfigHandler.getPluginName(), newUUID, oldPoints);
    }

    private void copyLuckPerms(UUID oldUUID, UUID newUUID) {
        if (CorePlusAPI.getDepend().LuckPermsEnabled())
            return;
        CorePlusAPI.getCmd().dispatchConsoleCmd(ConfigHandler.getPluginName(),
                "lp user " + oldUUID.toString() + " clone " + newUUID.toString());
    }

    private void convertResidence(UUID oldUUID, UUID newUUID) {
        if (!UtilsHandler.getDepend().ResidenceEnabled())
            return;
        ResidencePlayer oldPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(oldUUID);
        ResidencePlayer newPlayer = Residence.getInstance().getPlayerManager().getResidencePlayer(newUUID);
        for (ClaimedResidence residence : oldPlayer.getResList()) {
            newPlayer.addResidence(residence);
            oldPlayer.removeResidence(residence);
        }
    }
    */
