package tfar.kothcrown.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.kothcrown.KingOfTheHillMenu;

public class C2SSetTablePacket implements C2SModPacket {

    public final String lootTable;
    public final boolean set;
    public C2SSetTablePacket(FriendlyByteBuf buf) {
        lootTable = buf.readUtf();
        set = buf.readBoolean();
    }

    public C2SSetTablePacket(String lootTable,boolean set) {
        this.lootTable = lootTable;
        this.set = set;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        if (player.containerMenu instanceof KingOfTheHillMenu menu) {
            menu.setCurrentString(lootTable,set);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeUtf(lootTable);
        to.writeBoolean(set);
    }
}
