package tfar.kothcrown.datagen.data;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import tfar.kothcrown.Init;
import tfar.kothcrown.KothCrown;

public class ModBlockLoot extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(Init.KING_OF_THE_HILL);
        dropSelf(Init.THRONE);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return KothCrown.getKnownBlocks().toList();
    }
}
