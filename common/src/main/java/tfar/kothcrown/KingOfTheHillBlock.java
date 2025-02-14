package tfar.kothcrown;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KingOfTheHillBlock extends Block implements EntityBlock {
    public KingOfTheHillBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KingOfTheHillBlockEntity(blockPos,blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (pPlayer.canUseGameMasterBlocks()) {
            pPlayer.openMenu((KingOfTheHillBlockEntity)blockentity);
            if (!pLevel.isClientSide) {
                ServerPlayer serverPlayer = (ServerPlayer) pPlayer;
                serverPlayer.connection.send(
                        new ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action.SET, List.of("test","example")));
            }


            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState $$1, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return (pLevel, pPos, pState, pBlockEntity) -> KingOfTheHillBlockEntity.serverTick(pLevel, pPos, pState, (KingOfTheHillBlockEntity) pBlockEntity);
        }
        return null;
    }
}
