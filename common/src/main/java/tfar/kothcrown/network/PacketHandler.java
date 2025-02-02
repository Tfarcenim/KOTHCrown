package tfar.kothcrown.network;

import net.minecraft.resources.ResourceLocation;
import tfar.kothcrown.KothCrown;
import tfar.kothcrown.network.server.C2SSetTablePacket;
import tfar.kothcrown.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerServerPacket(C2SSetTablePacket.class, C2SSetTablePacket::new);

    }

    public static ResourceLocation packet(Class<?> clazz) {
        return KothCrown.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
