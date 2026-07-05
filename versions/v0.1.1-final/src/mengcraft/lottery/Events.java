/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package com.mengcraft.lottery;

import com.mengcraft.lottery.DataCompond;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events
implements Listener {
    private final DataCompond compond;

    @EventHandler
    public void handle(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() == DataCompond.CHEST_HOLDER) {
            List<ItemStack> stacks = this.stacks(e.getInventory());
            String name = e.getInventory().getTitle();
            this.compond.update(name, stacks);
        }
    }

    @EventHandler
    public void handle(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == DataCompond.PREVIEW_HOLDER) {
            e.setCancelled(true);
            return;
        }
        if (e.getInventory().getHolder() == DataCompond.MENU_HOLDER && e.getRawSlot() < 27) {
            e.setCancelled(true);
            String n = e.getCurrentItem().getItemMeta().getDisplayName();
            this.compond.open(e.getWhoClicked(), n);
        }
    }

    private List<ItemStack> stacks(Inventory inventory) {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        for (ItemStack stack : inventory) {
            this.add(stacks, stack);
        }
        return stacks;
    }

    private void add(List<ItemStack> stacks, ItemStack stack) {
        if (stack != null && stack.getTypeId() != 0) {
            stacks.add(stack);
        }
    }

    public Events(DataCompond compond) {
        this.compond = compond;
    }
}
