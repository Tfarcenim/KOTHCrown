package tfar.kothcrown.datagen.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.kothcrown.KothCrown;
import tfar.kothcrown.ThroneInventory;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, KothCrown.MOD_ID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ThroneInventory.TAXABLE)
                .addTags(ItemTags.DIRT,ItemTags.LOGS,ItemTags.STONE_CRAFTING_MATERIALS, Tags.Items.RAW_MATERIALS,Tags.Items.GEMS)
                .add(Items.REDSTONE);

    }
}
