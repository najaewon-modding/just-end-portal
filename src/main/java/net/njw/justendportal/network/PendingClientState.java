package net.njw.justendportal.network;

import net.minecraft.world.level.Level;

public final class PendingClientState {
    private static boolean overworld;
    private static boolean end;
    private PendingClientState() {}
    public static boolean hasEntry(net.minecraft.resources.ResourceKey<Level> dimension) { return dimension == Level.END ? end : dimension == Level.OVERWORLD && overworld; }
    public static void set(boolean overworldValue, boolean endValue) { overworld = overworldValue; end = endValue; }
}
