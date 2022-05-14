package tw.momocraft.playerdataplus.playerstatus;

import org.bukkit.command.CommandSender;

public class PlayerStatusControl {

    private static boolean schedule = false;

    public static void startSchedule(CommandSender sender, String status) {
        /*
        if (ConfigHandler.getConfigPath().isPsCheckSchedule()) {
            CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                    "featureDisabled", sender);
            return;
        }
        boolean enable;
        if (status == null) {
            enable = !schedule;
        } else if (status.equals("on")) {
            enable = true;
        } else if (status.equals("off")) {
            enable = false;
        } else {
            enable = !schedule;
        }
        if (enable == schedule) {
            if (enable)
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                        ConfigHandler.getConfigPath().getMsgPSScheduleAlreadyStart(), sender);
            else
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                        ConfigHandler.getConfigPath().getMsgPSScheduleAlreadyEnd(), sender);
            return;
        }
        schedule = enable;
        if (!schedule)
            return;
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!schedule) {
                    cancel();
                    return;
                }
                CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                        ConfigHandler.getConfigPath().getMsgPSScheduleStart(), sender);
                checkAll();
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(),
                0, ConfigHandler.getConfigPath().getPsCheckScheduleInterval());
        CorePlusAPI.getMsg().sendLangMsg(ConfigHandler.getPluginName(),
                ConfigHandler.getConfigPath().getMsgPSScheduleEnd(), sender);
    }

    public static void checkAll() {
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                check(playerList.get(i));
                i++;
            }
        }.runTaskTimer(PlayerdataPlus.getInstance(), 0, 5);
    }

    public static void check(Player player) {
        if (player == null)
            return;
        Map<String, PlayerStatusMap> playerStatusProp = ConfigHandler.getConfigPath().getPlayerStatusProp();
        if (playerStatusProp == null)
            return;
        String playerName = player.getName();
        Location loc = player.getLocation();
        PlayerStatusMap playerStatusMap;
        back:
        for (String groupName : playerStatusProp.keySet()) {
            playerStatusMap = playerStatusProp.get(groupName);
            if (!isMatch(player, loc, playerStatusMap)) {
                CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                        "Player-Status" + groupName, "match", "continue", playerName,
                        new Throwable().getStackTrace()[0]);
                continue;
            }
            switch (groupName.toLowerCase()) {
                case "fly" -> {
                    if (!player.isFlying()) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status" + groupName, "flying", "continue", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    if (isAllowFly(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status" + groupName, "check", "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setFlying(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(), "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
                case "god" -> {
                    if (!CorePlusAPI.getDepend().CMIEnabled())
                        continue back;
                    CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
                    if (!user.isGod())
                        continue back;
                    if (isAllowGod(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status" + groupName, "check", "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    user.setGod(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
                case "op" -> {
                    if (!player.isOp())
                        continue back;
                    if (isAllowOP(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status" + groupName, "check", "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setOp(false);
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
                case "gamemode" -> {
                    if (!player.getGameMode().name().equalsIgnoreCase(ConfigHandler.getConfigPath().getPsGMDefault()))
                        continue back;
                    if (isAllowGM(player)) {
                        CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                                "Player-Status" + groupName, "check", "bypass", playerName,
                                new Throwable().getStackTrace()[0]);
                        continue back;
                    }
                    player.setGameMode(GameMode.valueOf(ConfigHandler.getConfigPath().getPsGMDefault().toUpperCase()));
                    CorePlusAPI.getMsg().sendDetailMsg(ConfigHandler.isDebug(), ConfigHandler.getPluginName(),
                            "Player-Status." + groupName, playerName, "World-Change", "cancel", "final",
                            new Throwable().getStackTrace()[0]);
                }
                default -> {
                    CorePlusAPI.getMsg().sendErrorMsg(ConfigHandler.getPluginName(),
                            "Can not find the player status type: " + groupName);
                }
            }
        }
    }

    public static boolean isSchedule() {
        return schedule;
    }

    public static void setSchedule(boolean schedule) {
        PlayerStatusControl.schedule = schedule;
    }

    private static boolean isMatch(Player player, Location loc, PlayerStatusMap playerStatusMap) {
        // Checking Location.
        if (!CorePlusAPI.getCond().checkLocation(ConfigHandler.getPluginName(), loc, playerStatusMap.getLocList(), true))
            return false;
        // Checking Conditions.
        return CorePlusAPI.getCond().checkCondition(ConfigHandler.getPluginName(),
                CorePlusAPI.getMsg().transHolder(null, player, playerStatusMap.getConditions()));
    }

    public static boolean isAllowFly(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.fly"))
            return true;
        if (CorePlusAPI.getDepend().ResidenceEnabled()) {
            return CorePlusAPI.getCond().checkFlag(player, player.getLocation(), "fly", true);
        }
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.fly"))
                return true;
            return CMI.getInstance().getPlayerManager().getUser(player).getTgod() > 0;
        }
        return false;
    }

    public static boolean isAllowGod(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.god"))
            return true;
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.god"))
                return true;
            return CMI.getInstance().getPlayerManager().getUser(player).getTgod() > 0;
        }
        return false;
    }

    public static boolean isAllowOP(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.op"))
            return true;
        return CorePlusAPI.getPlayer().hasPerm(player, "minecraft.command.op");
    }

    public static boolean isAllowGM(Player player) {
        if (CorePlusAPI.getPlayer().hasPerm(player, "playerdataplus.bypass.status.gamemode"))
            return true;
        if (CorePlusAPI.getPlayer().hasPerm(player, "minecraft.command.gamemode"))
            return true;
        if (CorePlusAPI.getDepend().CMIEnabled()) {
            if (CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.gm.*"))
                return true;
            return CorePlusAPI.getPlayer().hasPerm(player, "cmi.command.gm." + player.getGameMode().name().toLowerCase());
        }
        return false;
    }
         */
    }
}
