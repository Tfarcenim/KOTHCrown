package tfar.kothcrown;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class KingOfTheHillBlockEntity extends BlockEntity implements MenuProvider {

    protected ResourceLocation lootTable;
    protected long lootTableSeed;
    protected int delay = 20;
    protected int radius = 10;
    protected int tick;
    protected Container container = new SimpleContainer(54);

    protected boolean wasPlayerNearby;

    protected final ContainerData delayData = new ContainerData() {
        @Override
        public int get(int i) {
            switch (i) {
                case 0: return delay;
                case 1: return radius;
            }
            return -1;
        }

        @Override
        public void set(int i, int value) {
            switch (i) {
                case 0 -> {
                    delay = value;
                }
                case 1 -> {
                    radius = value;
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public KingOfTheHillBlockEntity(BlockPos pos, BlockState state) {
        super(Init.KING_OF_THE_HILL_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, KingOfTheHillBlockEntity pBlockEntity) {
        if (pBlockEntity.isPlayerNearby(pLevel,pPos)) {
            pBlockEntity.tick++;
            if (pBlockEntity.tick >= pBlockEntity.delay) {
                pBlockEntity.tick = 0;
                boolean b = pBlockEntity.dropItems();

                if(b && !pBlockEntity.wasPlayerNearby) {
                    pBlockEntity.wasPlayerNearby = true;
                    pLevel.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Player has activated King of the Hill block!"),false);
                }

            }
            pBlockEntity.setChanged();
        }
    }

    public boolean isPlayerNearby(Level pLevel, BlockPos pPos) {
        return pLevel.getNearestPlayer(pPos.getX(),pPos.getY(),pPos.getZ(),radius,false) != null;
    }

    protected boolean dropItems() {
        if (lootTable != null) {
            if (this.level.getServer() != null) {
                container.clearContent();
                LootTable loottable = this.level.getServer().getLootData().getLootTable(this.lootTable);

                LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));

                loottable.fill(container, lootparams$builder.create(LootContextParamSets.CHEST), this.lootTableSeed);
                Containers.dropContents(level,worldPosition,container);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (lootTable != null) {
            tag.putString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, lootTable.toString());
        }
        tag.putLong(RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG,lootTableSeed);
        tag.putInt("delay",delay);
        tag.putInt("tick",tick);
        tag.putInt("radius",radius);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(RandomizableContainerBlockEntity.LOOT_TABLE_TAG))
            lootTable = new ResourceLocation(tag.getString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG));
        lootTableSeed = tag.getLong(RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG);
        delay = tag.getInt("delay");
        tick = tag.getInt("tick");
        radius = tag.getInt("radius");
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("King of the Hill");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new KingOfTheHillMenu(i,inventory, ContainerLevelAccess.create(level,worldPosition),new SimpleContainerData(1),lootTable,delayData);
    }
}
