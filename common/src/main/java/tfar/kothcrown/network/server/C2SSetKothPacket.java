package tfar.kothcrown.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.kothcrown.KingOfTheHillMenu;

public class C2SSetKothPacket implements C2SModPacket {

    public final String lootTable;
    public final boolean set;
    public final int delay;
    private final int radius;

    public C2SSetKothPacket(FriendlyByteBuf buf) {
        lootTable = buf.readUtf();
        set = buf.readBoolean();
        delay = buf.readInt();
        radius = buf.readInt();
    }

    public C2SSetKothPacket(String lootTable, boolean set, int delay, int radius) {
        this.lootTable = lootTable;
        this.set = set;
        this.delay = delay;
        this.radius = radius;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player.containerMenu instanceof KingOfTheHillMenu menu) {
            menu.update(lootTable,set,delay,radius);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(lootTable);
        to.writeBoolean(set);
        to.writeInt(delay);
        to.writeInt(radius);
    }
}
