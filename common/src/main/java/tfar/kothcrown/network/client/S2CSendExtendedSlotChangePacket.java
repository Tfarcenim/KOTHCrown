package tfar.kothcrown.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.kothcrown.ThroneMenu;
import tfar.kothcrown.network.PacketBufferEX;


public class S2CSendExtendedSlotChangePacket implements S2CModPacket {

    int windowId;
    int slot;
    ItemStack stack;

    public S2CSendExtendedSlotChangePacket(int windowId, int slot, ItemStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSendExtendedSlotChangePacket(FriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = PacketBufferEX.readExtendedItemStack(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
    }

    @Override
    public void handleClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof ThroneMenu && windowId == player.containerMenu.containerId) {
            player.containerMenu.slots.get(slot).set(stack);
        }
    }
}