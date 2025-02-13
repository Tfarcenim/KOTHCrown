package tfar.kothcrown;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ThroneSlot extends SlotItemHandler {

    public ThroneSlot(ThroneInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return getItemHandler().getSlotLimit(getSlotIndex());
    }
}