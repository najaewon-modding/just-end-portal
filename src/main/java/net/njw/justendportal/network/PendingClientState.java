package net.njw.justendportal.network;

public final class PendingClientState {
    private static boolean pending;
    private PendingClientState() {}
    public static boolean hasPending() { return pending; }
    public static void setPending(boolean value) { pending = value; }
}
