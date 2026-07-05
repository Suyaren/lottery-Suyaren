/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.utility.StreamSerializer
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package com.mengcraft.lottery;

import com.comphenix.protocol.utility.StreamSerializer;
import com.mengcraft.lottery.AdminTask;
import com.mengcraft.lottery.ChestHolder;
import com.mengcraft.lottery.Lottery;
import com.mengcraft.lottery.Main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class DataCompond {
    public static final InventoryHolder CHEST_HOLDER;
    public static final InventoryHolder MENU_HOLDER;
    public static final InventoryHolder PREVIEW_HOLDER;
    public static final ItemStack AIR_STACK;
    public static final ItemStack[] EMPTY_ARRAY;
    public static final StreamSerializer SERIALIZER;
    private final Map<String, Lottery> entry;
    private final Main main;
    private final Inventory menu;
    private final Map<String, Inventory> admin;

    public void add(String string, Lottery lottery) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(string);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(ChatColor.GOLD + "\u8282\u70b9: " + string);
        arrayList.add(ChatColor.GOLD + "\u540d\u79f0: " + lottery.name());
        arrayList.add(ChatColor.GOLD + "\u51e0\u7387: " + lottery.chance());
        itemMeta.setLore(arrayList);
        itemStack.setItemMeta(itemMeta);
        this.menu.addItem(new ItemStack[]{itemStack});
        Inventory inventory = this.server().createInventory(CHEST_HOLDER, 54, string);
        inventory.setContents(lottery.stacks());
        this.admin.put(string, inventory);
        this.entry.put(string, lottery);
    }

    public Lottery getLottery(String string) {
        Lottery lottery = this.entry.get(string);
        if (lottery != null) {
            return lottery;
        }
        for (String string2 : this.entry.keySet()) {
            if (!string2.endsWith("." + string)) continue;
            return this.entry.get(string2);
        }
        return null;
    }

    public void update(String string, List<ItemStack> list) {
        if (this.admin.get(string) == null) {
            return;
        }
        ItemStack[] itemStackArray = list.toArray(EMPTY_ARRAY);
        this.admin.get(string).setContents(itemStackArray);
        this.entry.get(string).stacks(list);
        String string2 = "global." + string + ".items";
        List<String> list2 = this.serialize(itemStackArray);
        this.main.getConfig().set(string2, list2);
        this.main.saveConfig();
    }

    public Inventory menu() {
        return this.menu;
    }

    public void save() {
        Collection<Lottery> collection = this.entry.values();
        for (Lottery lottery : collection) {
            String string = "global." + lottery.node();
            this.main.getConfig().set(string + ".name", (Object)lottery.name());
            this.main.getConfig().set(string + ".chance", (Object)lottery.chance());
            this.main.getConfig().set(string + ".show", (Object)lottery.show());
            this.main.getConfig().set(string + ".items", this.serialize(lottery.stacks()));
        }
        this.main.saveConfig();
    }

    private List<String> serialize(ItemStack[] itemStackArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (ItemStack itemStack : itemStackArray) {
            try {
                String string = SERIALIZER.serializeItemStack(itemStack);
                arrayList.add(string);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return arrayList;
    }

    public Inventory admin(String string) {
        return this.admin.get(string);
    }

    public void register(Listener listener) {
        this.main.getServer().getPluginManager().registerEvents(listener, (Plugin)this.main);
    }

    public void broad(String string) {
        this.main.getServer().broadcast(string, "bukkit.broadcast.user");
    }

    public String message(String string) {
        return this.main.getConfig().getString("message." + string);
    }

    public Server server() {
        return this.main.getServer();
    }

    public Lottery roll() {
        Collection<Lottery> collection = this.entry.values();
        for (Lottery lottery : collection) {
            if (lottery.chance() <= 1 || lottery.roll() <= 0) continue;
            return lottery;
        }
        for (Lottery lottery : collection) {
            if (lottery.chance() != 1) continue;
            return lottery;
        }
        return null;
    }

    public void task(Runnable runnable) {
        this.server().getScheduler().runTask((Plugin)this.main, runnable);
    }

    public DataCompond(Main main) {
        this.main = main;
        this.entry = new HashMap<String, Lottery>();
        this.admin = new HashMap<String, Inventory>();
        this.menu = main.getServer().createInventory(MENU_HOLDER, 27);
    }

    public void open(HumanEntity humanEntity, String string) {
        this.task(new AdminTask(this, humanEntity, string));
    }

    public void showPreview(Player player, Lottery lottery) {
        Inventory inv = this.server().createInventory(PREVIEW_HOLDER, 54, lottery.name());
        ItemStack[] stacks = lottery.stacks();
        for (int i = 0; i < stacks.length && i < 54; i++) {
            inv.setItem(i, stacks[i]);
        }
        player.openInventory(inv);
    }

    public void reload() {
        this.main.reloadConfig();
        this.entry.clear();
        this.admin.clear();
        this.menu.clear();
        for (String string : this.main.getKeys("global")) {
            int n = this.main.getChance(string);
            String string2 = this.main.getName(string);
            ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
            this.main.fill(arrayList, this.main.getStacks(string));
            boolean bl = this.main.getShow(string);
            Lottery lottery = new Lottery(string, string2, n, arrayList, bl);
            this.add(string, lottery);
        }
    }

    static {
        SERIALIZER = new StreamSerializer();
        EMPTY_ARRAY = new ItemStack[0];
        CHEST_HOLDER = new ChestHolder();
        MENU_HOLDER = new ChestHolder();
        PREVIEW_HOLDER = new ChestHolder();
        AIR_STACK = new ItemStack(Material.AIR);
    }
}
