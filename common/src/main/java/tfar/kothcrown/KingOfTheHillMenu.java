package tfar.kothcrown;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import tfar.kothcrown.platform.Services;

import java.util.Optional;

public class KingOfTheHillMenu extends AbstractContainerMenu {

    protected ResourceLocation oldString;
    protected String currentString;
    private final ContainerLevelAccess access;
    protected final ContainerData data;

    public KingOfTheHillMenu(int id, Inventory inventory, ContainerLevelAccess access, ContainerData data, ResourceLocation oldString) {
        super(Init.KING_OF_THE_HILL_MENU, id);
        this.access = access;
        this.data = data;
        this.oldString = oldString;
        addDataSlots(data);
    }

    public KingOfTheHillMenu(int i, Inventory inventory) {
        this(i,inventory, ContainerLevelAccess.NULL, new SimpleContainerData(1),new ResourceLocation("null"));
    }

    public void setCurrentString(String currentString, boolean set) {
        this.currentString = currentString;
        try {
            ResourceLocation location = new ResourceLocation(currentString);
            if (set) {
                access.execute((level, pos) -> {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof KingOfTheHillBlockEntity kingOfTheHillBlockEntity) {
                        kingOfTheHillBlockEntity.lootTable = location;
                        kingOfTheHillBlockEntity.setChanged();
                    }
                });
            } else {
                LootTable evaluate = access.evaluate((level, pos) -> level.getServer().getLootData().getLootTable(location)).get();
                data.set(0, Services.PLATFORM.countPools(evaluate) != 0  ? 0xffffffff : 0xffffff00);
            }

        } catch (Exception e) {
            data.set(0,0xffff0000);
        }
    }

    public int getColor() {
        return data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
