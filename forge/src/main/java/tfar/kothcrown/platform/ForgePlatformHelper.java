package tfar.kothcrown.platform;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import tfar.kothcrown.PacketHandlerForge;
import tfar.kothcrown.ThroneInventory;
import tfar.kothcrown.ThroneMenu;
import tfar.kothcrown.ThroneSavedData;
import tfar.kothcrown.mixin.LootTableMixinForge;
import tfar.kothcrown.network.client.S2CModPacket;
import tfar.kothcrown.network.server.C2SModPacket;
import tfar.kothcrown.network.server.C2SSetTaxRatePacket;
import tfar.kothcrown.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Function;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    int i;

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapS2C());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapC2S());
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketHandlerForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketHandlerForge.sendToServer(msg);
    }

    @Override
    public void nineSlice(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, int sliceSize, int uWidth, int vHeight, int uOffset, int vOffset,
                          int textureWidth, int textureHeight) {
        guiGraphics.blitNineSlicedSized(texture,x,y,width,height,sliceSize,uWidth,vHeight,uOffset,vOffset,textureWidth,textureHeight);
    }

    @Override
    public int countPools(LootTable lootTable) {
        return ((LootTableMixinForge)lootTable).getPools().size();
    }

    @Override
    public MenuType<?> createType() {
        return new MenuType<>(ThroneMenu::new, FeatureFlags.VANILLA_SET);
    }

    @Override
    public boolean onItemStackedOn(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess) {
        return ForgeHooks.onItemStackedOn(carriedItem, stackedOnItem, slot, action, player, carriedSlotAccess);
    }

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        ThroneInventory throneInventory = ThroneSavedData.getOrCreateDefaultInstance(player.getServer()).getThroneInventory();
        return new ThroneMenu(i,inventory,throneInventory);
    }

    @Override
    public void handle(ServerPlayer player, C2SSetTaxRatePacket c2SSetTaxRatePacket) {
        ThroneInventory throneInventory = ThroneSavedData.getOrCreateDefaultInstance(player.getServer()).getThroneInventory();
        throneInventory.setTaxRate(c2SSetTaxRatePacket.d);
    }
}