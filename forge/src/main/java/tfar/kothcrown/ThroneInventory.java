package tfar.kothcrown;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ThroneInventory implements IItemHandlerModifiable {

    public static final int MAX_SLOT_COUNT = 2_000_000_000;

    private final ThroneSavedData data;

    protected List<ItemStack> stacks = new ArrayList<>();

    public ThroneInventory(ThroneSavedData data) {
        this.data = data;
    }

    @Override
    public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
        if (isSlotValid(i)) {
            stacks.set(i,itemStack);
        }
    }

    @Override
    public int getSlots() {
        return stacks.size() < MAX_SLOT_COUNT ? stacks.size()+1 : MAX_SLOT_COUNT;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return isSlotValid(slot) ? stacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(slot, stack)) {
            return stack;
        } else {
           // this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            int limit = MAX_SLOT_COUNT;//this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    private void onContentsChanged(int slot) {
        data.setDirty();
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
          //  this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.stacks.set(slot, ItemStack.EMPTY);
                        this.onContentsChanged(slot);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                        this.onContentsChanged(slot);
                    }

                    return existing.copyWithCount(toExtract);
                }
            }
        }
    }

    @Override
    public int getSlotLimit(int i) {
        return 2_000_000_000;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        return isSlotValid(slot);
    }

    protected boolean isSlotValid(int slot) {
        return slot >= 0 && slot < stacks.size();
    }

    public CompoundTag save() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
                int realCount = stack.getCount();
                CompoundTag itemTag = new CompoundTag();
              //  itemTag.putInt("Slot", i);
                stack.save(itemTag);
                itemTag.putInt("ExtendedCount", realCount);
                nbtTagList.add(itemTag);
        }


        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        return nbt;
    }

    public void read(CompoundTag nbt) {
        stacks.clear();
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            ItemStack stack = ItemStack.of(itemTags);
            if (itemTags.contains("ExtendedCount", Tag.TAG_INT)) {
                stack.setCount(itemTags.getInt("ExtendedCount"));
            }
            stacks.add(stack);
        }
    }
}
