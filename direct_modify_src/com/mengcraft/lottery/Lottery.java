/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.mengcraft.lottery;

import com.mengcraft.lottery.DataCompond;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.ItemStack;

public class Lottery {
    private final String name;
    private final int chance;
    private List<ItemStack> stacks;
    private final String node;
    private final boolean show;

    public String node() {
        return this.node;
    }

    public int chance() {
        return this.chance;
    }

    public void stacks(List<ItemStack> list) {
        this.stacks = list;
    }

    public ItemStack[] stacks() {
        return this.stacks.toArray(DataCompond.EMPTY_ARRAY);
    }

    public String name() {
        return this.name;
    }

    public boolean show() {
        return this.show;
    }

    public int roll() {
        return new Random().nextInt(this.chance) < 1 ? 1 : 0;
    }

    public ItemStack select() {
        if (this.stacks.size() > 0) {
            return this.stacks.get(new Random().nextInt(this.stacks.size()));
        }
        return DataCompond.AIR_STACK;
    }

    public Lottery(String string, String string2, int n, List<ItemStack> list) {
        this(string, string2, n, list, true);
    }

    public Lottery(String string, String string2, int n, List<ItemStack> list, boolean bl) {
        this.node = string;
        this.name = string2;
        this.chance = n;
        this.stacks = list;
        this.show = bl;
    }
}
