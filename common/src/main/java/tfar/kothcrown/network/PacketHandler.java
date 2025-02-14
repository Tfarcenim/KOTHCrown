package tfar.kothcrown.network;

import net.minecraft.resources.ResourceLocation;
import tfar.kothcrown.KothCrown;
import tfar.kothcrown.network.client.S2CInitialSyncContainerPacket;
import tfar.kothcrown.network.client.S2CSendExtendedSlotChangePacket;
import tfar.kothcrown.network.server.C2SSetKothPacket;
import tfar.kothcrown.network.server.C2SSetTaxRatePacket;
import tfar.kothcrown.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {
        Services.PLATFORM.registerServerPacket(C2SSetKothPacket.class, C2SSetKothPacket::new);
        Services.PLATFORM.registerServerPacket(C2SSetTaxRatePacket.class, C2SSetTaxRatePacket::new);

        Services.PLATFORM.registerClientPacket(S2CSendExtendedSlotChangePacket.class, S2CSendExtendedSlotChangePacket::new);
        Services.PLATFORM.registerClientPacket(S2CInitialSyncContainerPacket.class, S2CInitialSyncContainerPacket::new);


    }

    public static ResourceLocation packet(Class<?> clazz) {
        return KothCrown.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
