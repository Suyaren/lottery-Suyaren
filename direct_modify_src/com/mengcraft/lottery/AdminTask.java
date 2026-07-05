/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 */
package com.mengcraft.lottery;

import com.mengcraft.lottery.DataCompond;
import org.bukkit.entity.HumanEntity;

public class AdminTask
implements Runnable {
    private final DataCompond compond;
    private final HumanEntity who;
    private final String name;

    @Override
    public void run() {
        this.who.openInventory(this.compond.admin(this.name));
    }

    public AdminTask(DataCompond compond, HumanEntity who, String name) {
        this.compond = compond;
        this.who = who;
        this.name = name;
    }
}
