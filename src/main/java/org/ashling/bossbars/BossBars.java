package org.ashling.bossbars;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BossBars extends JavaPlugin implements CommandExecutor {

    private final Map<UUID, List<BossBar>> activeBossBars = new HashMap<>();

    @Override
    public void onEnable() {
        this.getCommand("bossbar").setExecutor(this);
        this.getCommand("bossbarstop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("bossbar")) {
            if (args.length < 1) {
                sender.sendMessage("Usage: /bossbar <message> [level]");
                return true;
            }

            String message = args[0];
            int level = 1;

            if (args.length >= 2) {
                try {
                    level = Math.max(1, Math.min(Integer.parseInt(args[1]), 6));
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid level. Using default.");
                }
            }

            sendBossBarMessage(player, message, level);
            return true;
        } else if (command.getName().equalsIgnoreCase("bossbarstop")) {
            removeBossBar(player);
            return true;
        }

        return false;
    }

    private void sendBossBarMessage(Player player, String message, int level) {
        clearExistingBossBars(player);
        List<BossBar> playerBossBars = new ArrayList<>();

        for (int i = 1; i < level; i++) {
            BossBar invisibleBar = this.getServer().createBossBar(" ", BarColor.WHITE, BarStyle.SOLID);
            invisibleBar.addPlayer(player);
            invisibleBar.setVisible(true);
            invisibleBar.setProgress(0.0);
            playerBossBars.add(invisibleBar);
        }

        BossBar messageBar = this.getServer().createBossBar(
                net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message),
                BarColor.WHITE, BarStyle.SOLID);
        messageBar.addPlayer(player);
        messageBar.setVisible(true);
        playerBossBars.add(messageBar);

        activeBossBars.put(player.getUniqueId(), playerBossBars);
    }

    private void clearExistingBossBars(Player player) {
        List<BossBar> bars = activeBossBars.get(player.getUniqueId());
        if (bars != null) {
            bars.forEach(bossBar -> bossBar.removePlayer(player));
            bars.clear();
        }
    }

    private void removeBossBar(Player player) {
        clearExistingBossBars(player);
        activeBossBars.remove(player.getUniqueId());
    }
}
