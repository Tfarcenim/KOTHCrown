package tfar.kothcrown;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import tfar.kothcrown.platform.Services;

public class KingOfTheHillMenu extends AbstractContainerMenu {

    protected ResourceLocation oldString;
    protected final ContainerData extraData;
    protected String currentString;

    private final ContainerLevelAccess access;
    protected final ContainerData data;

    public KingOfTheHillMenu(int id, Inventory inventory, ContainerLevelAccess access, ContainerData data, ResourceLocation oldString, ContainerData extraData) {
        super(Init.KING_OF_THE_HILL_MENU, id);
        this.access = access;
        this.data = data;
        this.oldString = oldString;
        this.extraData = extraData;
        addDataSlots(data);
        addDataSlots(extraData);
    }

    @Override
    public void setData(int $$0, int $$1) {
        super.setData($$0, $$1);
        broadcastChanges();
    }

    public KingOfTheHillMenu(int i, Inventory inventory) {
        this(i,inventory, ContainerLevelAccess.NULL, new SimpleContainerData(1),new ResourceLocation("null"), new SimpleContainerData(2));
    }

    public void update(String currentString, boolean set, int delay,int radius) {

        if (delay >0 && set) {
            access.execute((level, pos) -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof KingOfTheHillBlockEntity kingOfTheHillBlockEntity) {
                    kingOfTheHillBlockEntity.delay = delay;
                    kingOfTheHillBlockEntity.setChanged();
                }
            });
        }

        if (radius >0 && set) {
            access.execute((level, pos) -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof KingOfTheHillBlockEntity kingOfTheHillBlockEntity) {
                    kingOfTheHillBlockEntity.radius = radius;
                    kingOfTheHillBlockEntity.setChanged();
                }
            });
        }


        this.currentString = currentString;
        try {
            ResourceLocation location = new ResourceLocation(currentString);
            if (set) {
                access.execute((level, pos) -> {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof KingOfTheHillBlockEntity kingOfTheHillBlockEntity && !currentString.isBlank()) {
                        kingOfTheHillBlockEntity.lootTable = location;
                        kingOfTheHillBlockEntity.setChanged();
                    }
                });
            } else {
                LootTable evaluate = access.evaluate((level, pos) -> level.getServer().getLootData().getLootTable(location)).get();
                data.set(0, Services.PLATFORM.countPools(evaluate) != 0  ? 0xffffffff : 0xffffff00);
            }

        } catch (Exception e) {
            data.set(0,0xff000000|0xff0000);
        }
    }

    public int getDelay() {
        return extraData.get(0);
    }

    public int getRadius() {
        return extraData.get(1);
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
