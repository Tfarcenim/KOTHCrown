package tfar.kothcrown;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Init {
    public static final Item CROWN = new CrownItem(new Item.Properties());

    public static final Block KING_OF_THE_HILL = new KingOfTheHillBlock(BlockBehaviour.Properties.of().strength(-1,6000000));
    public static final Item KING_OF_THE_HILL_ITEM = new BlockItem(KING_OF_THE_HILL,new Item.Properties());
    public static final BlockEntityType<KingOfTheHillBlockEntity> KING_OF_THE_HILL_BLOCK_ENTITY = BlockEntityType.Builder.of(KingOfTheHillBlockEntity::new,KING_OF_THE_HILL).build(null);
    public static final MenuType<KingOfTheHillMenu> KING_OF_THE_HILL_MENU = new MenuType<>(KingOfTheHillMenu::new, FeatureFlags.VANILLA_SET);

    public static final Block THRONE = new ThroneBlock(BlockBehaviour.Properties.of().strength(1,1));
    public static final Item THRONE_ITEM = new BlockItem(THRONE,new Item.Properties());
    public static final MenuType<ThroneMenu> THRONE_MENU = new MenuType<>(ThroneMenu::new,FeatureFlags.VANILLA_SET);
}
