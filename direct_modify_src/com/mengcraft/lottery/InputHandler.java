/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.mengcraft.lottery;

import com.mengcraft.lottery.DataCompond;
import com.mengcraft.lottery.Lottery;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InputHandler
implements CommandExecutor {
    private final DataCompond compond;

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        Player player = (Player)commandSender;
        if (stringArray.length < 1 && player.hasPermission("lottery.use")) {
            this.roll(player);
        } else if (stringArray.length != 0) {
            if (stringArray[0].equalsIgnoreCase("set") || stringArray[0].equalsIgnoreCase("save") || stringArray[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("lottery.admin")) {
                    this.admin(player, stringArray[0]);
                } else {
                    player.sendMessage(ChatColor.RED + "\u6ca1\u6709\u6743\u9650");
                }
            } else if (stringArray[0].equalsIgnoreCase("show")) {
                if (player.hasPermission("lottery.use")) {
                    this.show(player, stringArray.length > 1 ? stringArray[1] : "");
                } else {
                    player.sendMessage(ChatColor.RED + "\u6ca1\u6709\u6743\u9650");
                }
            } else if (stringArray[0].toLowerCase().startsWith("show")) {
                if (player.hasPermission("lottery.use")) {
                    this.show(player, stringArray[0].substring(4));
                } else {
                    player.sendMessage(ChatColor.RED + "\u6ca1\u6709\u6743\u9650");
                }
            } else if (player.hasPermission("lottery.use")) {
                this.give(player, stringArray[0]);
            } else {
                player.sendMessage(ChatColor.RED + "\u6ca1\u6709\u6743\u9650");
            }
        }
        return true;
    }

    private void admin(Player player, String string) {
        if (string.equals("set")) {
            player.openInventory(this.compond.menu());
        } else if (string.equals("save")) {
            this.compond.save();
            player.sendMessage(ChatColor.GREEN + "\u914d\u7f6e\u5df2\u4fdd\u5b58");
        } else if (string.equals("reload")) {
            this.compond.reload();
            player.sendMessage(ChatColor.GREEN + "\u914d\u7f6e\u5df2\u91cd\u8f7d");
        }
    }

    private void roll(Player player) {
        Lottery lottery = this.compond.roll();
        if (lottery != null) {
            ItemStack itemStack = lottery.select();
            player.getInventory().addItem(new ItemStack[]{itemStack});
            String string = itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
            String string2 = this.compond.message("success").replace("%P", player.getName()).replace("%L", lottery.name()).replace("%I", string);
            player.sendMessage(string2);
            if (lottery.show()) {
                String string3 = this.compond.message("item").replace("%P", player.getName()).replace("%L", lottery.name()).replace("%I", string);
                this.compond.broad(string3);
            }
        } else {
            player.sendMessage(this.compond.message("fail"));
        }
    }

    private void give(Player player, String string) {
        Lottery lottery = this.compond.getLottery(string);
        if (lottery == null) {
            player.sendMessage(ChatColor.RED + "\u5956\u52b1\u7ec4\u4e0d\u5b58\u5728: " + string);
            return;
        }
        ItemStack itemStack = lottery.select();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "\u5956\u52b1\u7ec4\u4e3a\u7a7a: " + lottery.name());
            return;
        }
        ItemStack[] itemStackArray = player.getInventory().addItem(new ItemStack[]{itemStack}).values().toArray(new ItemStack[0]);
        if (itemStackArray.length > 0) {
            for (ItemStack itemStack2 : itemStackArray) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack2);
            }
            player.sendMessage(ChatColor.RED + "\u80cc\u5305\u5df2\u6ee1\uff0c\u7269\u54c1\u5df2\u6389\u843d");
        } else {
            String string2 = itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
            String string3 = this.compond.message("success").replace("%P", player.getName()).replace("%L", lottery.name()).replace("%I", string2);
            player.sendMessage(string3);
            if (lottery.show()) {
                String string4 = this.compond.message("item").replace("%P", player.getName()).replace("%L", lottery.name()).replace("%I", string2);
                this.compond.broad(string4);
            }
        }
    }

    private void show(Player player, String groupName) {
        if (groupName.isEmpty()) {
            player.sendMessage(ChatColor.RED + "\u5956\u52b1\u7ec4\u4e0d\u5b58\u5728");
            return;
        }
        Lottery lottery = this.compond.getLottery(groupName);
        if (lottery == null) {
            player.sendMessage(ChatColor.RED + "\u5956\u52b1\u7ec4\u4e0d\u5b58\u5728: " + groupName);
            return;
        }
        this.compond.showPreview(player, lottery);
    }

    public InputHandler(DataCompond dataCompond) {
        this.compond = dataCompond;
    }
}
