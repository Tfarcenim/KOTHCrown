package tfar.kothcrown.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.kothcrown.KothCrown;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.kothcrown.ThroneInventory;

@Mixin(Inventory.class)
public class InventoryMixinForge {
    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z",at = @At("HEAD"))
    private void onItemPickup(ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        ThroneInventory.onItemPickup((Inventory)(Object)this,pStack);
    }
}