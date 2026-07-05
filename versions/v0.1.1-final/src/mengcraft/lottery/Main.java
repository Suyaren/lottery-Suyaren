/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.utility.StreamSerializer
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.mengcraft.lottery;

import com.comphenix.protocol.utility.StreamSerializer;
import com.mengcraft.lottery.DataCompond;
import com.mengcraft.lottery.Events;
import com.mengcraft.lottery.InputHandler;
import com.mengcraft.lottery.Lottery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main
extends JavaPlugin {
    private final StreamSerializer s = DataCompond.SERIALIZER;

    public void onEnable() {
        DataCompond dataCompond = new DataCompond(this);
        this.saveResource("config.yml", false);
        try {
            this.getConfig().load(new File(this.getDataFolder(), "config.yml"));
        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        catch (InvalidConfigurationException invalidConfigurationException) {
            invalidConfigurationException.printStackTrace();
        }
        for (String string : this.getKeys("global")) {
            int n = this.getChance(string);
            String string2 = this.getName(string);
            ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
            this.fill(arrayList, this.getStacks(string));
            boolean bl = this.getShow(string);
            Lottery lottery = new Lottery(string, string2, n, arrayList, bl);
            dataCompond.add(string, lottery);
        }
        dataCompond.register(new Events(dataCompond));
        this.getCommand("cj").setExecutor((CommandExecutor)new InputHandler(dataCompond));
        String[] stringArray = new String[]{ChatColor.GREEN + "\u68a6\u68a6\u5bb6\u9ad8\u6027\u80fd\u670d\u52a1\u5668\u51fa\u79df\u5e97", ChatColor.GREEN + "shop105595113.taobao.com"};
        this.getServer().getConsoleSender().sendMessage(stringArray);
    }

    public String getName(String string) {
        return this.getConfig().getString("global." + string + ".name");
    }

    public void fill(List<ItemStack> list, List<String> list2) {
        for (String string : list2) {
            try {
                ItemStack itemStack = this.s.deserializeItemStack(string);
                list.add(itemStack);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public List<String> getStacks(String string) {
        return this.getConfig().getStringList("global." + string + ".items");
    }

    public int getChance(String string) {
        return this.getConfig().getInt("global." + string + ".chance");
    }

    public boolean getShow(String string) {
        return this.getConfig().getBoolean("global." + string + ".show", true);
    }

    public Set<String> getKeys(String string) {
        if (this.getConfig().getConfigurationSection(string) != null) {
            return this.getConfig().getConfigurationSection(string).getKeys(false);
        }
        return new HashSet<String>();
    }
}
