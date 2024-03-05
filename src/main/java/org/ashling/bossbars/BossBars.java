package org.ashling.bossbars;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBars extends JavaPlugin implements CommandExecutor {

    private final Map<UUID, BossBar> activeBossBars = new HashMap<>();

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

        // Stack invisible boss bars first based on the specified level.
        for (int i = 1; i < level; i++) {
            BossBar invisibleBar = this.getServer().createBossBar(
                    " ", BarColor.WHITE, BarStyle.SOLID);
            invisibleBar.addPlayer(player);
            invisibleBar.setVisible(true);
            invisibleBar.setProgress(0.0);
            activeBossBars.put(player.getUniqueId(), invisibleBar);
        }

        // Create the visible boss bar for the message last so it's positioned below the invisible ones.
        BossBar messageBar = this.getServer().createBossBar(
                net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message),
                BarColor.WHITE, BarStyle.SOLID);
        messageBar.addPlayer(player);
        messageBar.setVisible(true);
        activeBossBars.put(player.getUniqueId(), messageBar);
    }

    private void clearExistingBossBars(Player player) {
        activeBossBars.values().forEach(bossBar -> bossBar.removePlayer(player));
        activeBossBars.clear();
    }

    private void removeBossBar(Player player) {
        clearExistingBossBars(player);
    }
}
