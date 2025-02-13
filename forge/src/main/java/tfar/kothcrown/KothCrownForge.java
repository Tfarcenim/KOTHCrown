package tfar.kothcrown;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import tfar.kothcrown.datagen.ModDatagen;

@Mod(KothCrown.MOD_ID)
public class KothCrownForge {
    
    public KothCrownForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,KothCrownConfig.SERVER_SPEC);
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
        bus.addListener(this::register);
        bus.addListener(ModDatagen::gather);

        if (FMLEnvironment.dist.isClient()) {
            ModClientForge.init(bus);
        }

        // Use Forge to bootstrap the Common mod.
        KothCrown.init();
        
    }

    void register(RegisterEvent event) {
        event.register(Registries.BLOCK,KothCrown.id("king_of_the_hill"),() -> Init.KING_OF_THE_HILL);
        event.register(Registries.ITEM,KothCrown.id("king_of_the_hill"),() -> Init.KING_OF_THE_HILL_ITEM);
        event.register(Registries.BLOCK_ENTITY_TYPE,KothCrown.id("king_of_the_hill"),() -> Init.KING_OF_THE_HILL_BLOCK_ENTITY);
        event.register(Registries.MENU,KothCrown.id("king_of_the_hill"),() -> Init.KING_OF_THE_HILL_MENU);
        event.register(Registries.BLOCK,KothCrown.id("throne"),() -> Init.THRONE);
        event.register(Registries.ITEM,KothCrown.id("throne"),() -> Init.THRONE_ITEM);
        event.register(Registries.MENU,KothCrown.id("throne"),() -> Init.THRONE_MENU);
        event.register(Registries.ITEM,KothCrown.id("crown"),() -> Init.CROWN);
    }

}