package tfar.kothcrown.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import tfar.kothcrown.network.PacketBufferEX;

public class S2CInitialSyncContainerPacket implements S2CModPacket {

    private final int stateID;
    private final int windowId;
    private final NonNullList<ItemStack> stacks;
    private final ItemStack carried;

    public S2CInitialSyncContainerPacket(int stateID, int windowId, NonNullList<ItemStack> stacks, ItemStack carried) {
        this.stateID = stateID;
        this.windowId = windowId;
        this.stacks = stacks;
        this.carried = carried;
    }

    public S2CInitialSyncContainerPacket(FriendlyByteBuf buf) {
        stateID = buf.readInt();
        windowId = buf.readInt();
        carried = buf.readItem();
        int i = buf.readShort();
        stacks = NonNullList.withSize(i, ItemStack.EMPTY);
        for(int j = 0; j < i; ++j) {
            stacks.set(j, PacketBufferEX.readExtendedItemStack(buf));
        }
    }

    @Override
    public void handleClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && windowId == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(stateID, stacks, carried);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stateID);
        buf.writeInt(windowId);
        buf.writeItem(carried);
        buf.writeShort(stacks.size());
        for (ItemStack stack : stacks) {
            PacketBufferEX.writeExtendedItemStack(buf, stack);
        }
    }
}