package tfar.kothcrown.network.server;

import net.minecraft.server.level.ServerPlayer;
import tfar.kothcrown.network.ModPacket;

public interface C2SModPacket extends ModPacket {

    void handleServer(ServerPlayer player);

}
