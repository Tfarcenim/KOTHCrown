package tfar.kothcrown.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.kothcrown.platform.Services;

public class C2SSetTaxRatePacket implements C2SModPacket {

    public final double d;

    public C2SSetTaxRatePacket(double d) {
        this.d = d;
    }

    public C2SSetTaxRatePacket(FriendlyByteBuf buf) {
        this(buf.readDouble());
    }

    @Override
    public void handleServer(ServerPlayer player) {
        Services.PLATFORM.handle(player, this);
    }

    @Override
    public void write(FriendlyByteBuf to) {
        to.writeDouble(d);
    }
}
