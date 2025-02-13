package tfar.kothcrown;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.kothcrown.client.KingOfTheHillScreen;

public class ModClientForge {
    static void init(IEventBus bus) {
        bus.addListener(ModClientForge::setup);
    }

    static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(Init.KING_OF_THE_HILL_MENU, KingOfTheHillScreen::new);
        MenuScreens.register((MenuType<? extends ThroneMenu>) Init.THRONE_MENU, ThroneScreen::new);
    }

}
