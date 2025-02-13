package tfar.kothcrown;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tfar.kothcrown.platform.Services;

import java.text.DecimalFormat;

public class ThroneBlock extends Block {
    public ThroneBlock(Properties $$0) {
        super($$0);
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player player, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(Init.CROWN)) {
                player.openMenu($$0.getMenuProvider($$1, $$2));
            }
            //$$3.awardStat(Stats.INTERACT_WITH_ANVIL);
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Throne");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return Services.PLATFORM.createMenu(i, inventory, player);
            }
        };
    }

    public static String formatLargeNumber(int number) {

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }
}
